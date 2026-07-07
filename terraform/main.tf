provider "aws" {
  region = "us-east-1" # Cambia esto a tu región de AWS preferida
}

# ==========================================
# 1. Grupo de Seguridad para la instancia EC2
# ==========================================
resource "aws_cloudwatch_log_group" "gymstore_logs" {
  name              = "/ec2/gymstore-backend"
  retention_in_days = 7
}

resource "aws_security_group" "gymstore_sg" {
  name        = "gymstore-backend-sg"
  description = "Grupo de seguridad para el backend de GymStore (API y Usuario)"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Acceso publico API Catalogo"
  }

  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Acceso publico API Usuarios"
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Acceso SSH para despliegue automatizado"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Salida a internet"
  }
}

# ==========================================
# 2. Llave SSH autogenerada para GitHub Actions
# ==========================================
resource "tls_private_key" "gymstore_ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "gymstore_key_pair" {
  key_name   = "gymstore-deploy-key"
  public_key = tls_private_key.gymstore_ssh_key.public_key_openssh
}

# ==========================================
# 3. Instancia EC2 (Usando mys3Role)
# ==========================================
data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }
}

resource "aws_instance" "gymstore_backend" {
  ami                  = data.aws_ami.amazon_linux_2023.id
  instance_type        = "t3.medium" # Se cambia a t3.medium (4GB RAM) porque t2.micro (1GB) no soporta K3s + 2 Spring Boot + Postgres simultáneamente sin colapsar.
  
  iam_instance_profile = "LabInstanceProfile"
  key_name             = aws_key_pair.gymstore_key_pair.key_name

  vpc_security_group_ids = [aws_security_group.gymstore_sg.id]
  associate_public_ip_address = true

  user_data = <<-EOF
              #!/bin/bash
              dnf update -y
              dnf install amazon-cloudwatch-agent amazon-ssm-agent -y
              systemctl enable amazon-cloudwatch-agent
              systemctl start amazon-cloudwatch-agent
              systemctl enable amazon-ssm-agent
              systemctl start amazon-ssm-agent
              
              # Instalar K3s (Kubernetes ligero)
              curl -sfL https://get.k3s.io | sh -
              
              # Configurar K3s para que ec2-user pueda usar kubectl
              sleep 10
              mkdir -p /home/ec2-user/.kube
              cp /etc/rancher/k3s/k3s.yaml /home/ec2-user/.kube/config
              chown -R ec2-user:ec2-user /home/ec2-user/.kube
              echo 'export KUBECONFIG=/home/ec2-user/.kube/config' >> /home/ec2-user/.bashrc
              EOF

  tags = {
    Name = "GymStore-K8s-Cluster"
  }
}

# ==========================================
# 4. Outputs
# ==========================================
output "ec2_public_ip" {
  description = "Dirección IP pública de la EC2 (AGREGA EN GITHUB COMO 'EC2_HOST')"
  value       = aws_instance.gymstore_backend.public_ip
}

output "private_key_pem" {
  description = "Llave privada SSH (AGREGA EN GITHUB COMO 'EC2_SSH_KEY')"
  value       = tls_private_key.gymstore_ssh_key.private_key_pem
  sensitive   = true
}
