provider "aws" {
  region = var.aws_region # Vùng AWS sẽ tạo VPS
}

data "aws_ami" "ubuntu" {
  most_recent = true           # Lấy bản Ubuntu mới nhất
  owners      = ["099720109477"] # Canonical - chủ sở hữu Ubuntu AMI

  filter {
    name   = "name" # Lọc theo tên AMI
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"] # Ubuntu 22.04 amd64
  }

  filter {
    name   = "virtualization-type" # Lọc loại ảo hóa
    values = ["hvm"]               # Dùng HVM cho EC2 hiện đại
  }
}

resource "aws_vpc" "main" {
  cidr_block           = "10.30.0.0/16" # Dải mạng riêng cho cluster
  enable_dns_hostnames = true           # Bật DNS hostname trong VPC
  enable_dns_support   = true           # Bật DNS resolver trong VPC

  tags = {
    Name = "${var.project_name}-vpc" # Tên VPC trên AWS
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id # Gắn Internet Gateway vào VPC

  tags = {
    Name = "${var.project_name}-igw" # Tên Internet Gateway
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id # Subnet thuộc VPC chính
  cidr_block              = "10.30.1.0/24"  # Dải IP subnet public
  map_public_ip_on_launch = true            # EC2 tự nhận public IP

  tags = {
    Name = "${var.project_name}-public-subnet" # Tên subnet
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id # Route table cho subnet public

  route {
    cidr_block = "0.0.0.0/0"                 # Route ra Internet
    gateway_id = aws_internet_gateway.main.id # Đi qua Internet Gateway
  }

  tags = {
    Name = "${var.project_name}-public-rt" # Tên route table
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id      # Subnet public
  route_table_id = aws_route_table.public.id # Gắn route table vào subnet
}

resource "aws_security_group" "k3s" {
  name        = "${var.project_name}-k3s-sg" # Tên security group
  description = "k3s, app, monitoring access" # Mô tả rule truy cập
  vpc_id      = aws_vpc.main.id              # Security group thuộc VPC chính

  ingress {
    description = "SSH from admin IP"     # Chỉ cho IP admin SSH
    from_port   = 22                      # Port SSH
    to_port     = 22                      # Port SSH
    protocol    = "tcp"                   # Giao thức TCP
    cidr_blocks = [var.allowed_ssh_cidr]  # IP được phép SSH
  }

  ingress {
    description = "HTTP"          # Cho truy cập web HTTP
    from_port   = 80              # Port HTTP
    to_port     = 80              # Port HTTP
    protocol    = "tcp"           # Giao thức TCP
    cidr_blocks = ["0.0.0.0/0"]   # Mở public
  }

  ingress {
    description = "HTTPS"         # Cho truy cập web HTTPS
    from_port   = 443             # Port HTTPS
    to_port     = 443             # Port HTTPS
    protocol    = "tcp"           # Giao thức TCP
    cidr_blocks = ["0.0.0.0/0"]   # Mở public
  }

  ingress {
    description = "k3s API from admin IP" # Cho kubectl truy cập API server
    from_port   = 6443                    # Port Kubernetes API
    to_port     = 6443                    # Port Kubernetes API
    protocol    = "tcp"                   # Giao thức TCP
    cidr_blocks = [var.allowed_ssh_cidr]  # Chỉ IP admin
  }

  ingress {
    description = "Grafana NodePort demo" # Cho truy cập Grafana demo
    from_port   = 30300                   # NodePort Grafana
    to_port     = 30300                   # NodePort Grafana
    protocol    = "tcp"                   # Giao thức TCP
    cidr_blocks = [var.allowed_ssh_cidr]  # Chỉ IP admin
  }

  ingress {
    description = "Internal node communication" # Cho các VPS nói chuyện nội bộ
    from_port   = 0                             # Tất cả port
    to_port     = 0                             # Tất cả port
    protocol    = "-1"                          # Tất cả protocol
    self        = true                          # Chỉ trong cùng security group
  }

  egress {
    from_port   = 0             # Tất cả port outbound
    to_port     = 0             # Tất cả port outbound
    protocol    = "-1"          # Tất cả protocol outbound
    cidr_blocks = ["0.0.0.0/0"] # Cho ra Internet
  }

  tags = {
    Name = "${var.project_name}-k3s-sg" # Tên security group
  }
}


resource "aws_instance" "k3s" {
  count                       = 3                         # Tạo 3 VPS
  ami                         = data.aws_ami.ubuntu.id    # Dùng Ubuntu AMI
  instance_type               = var.instance_type         # Loại máy EC2
  key_name                    = var.key_name              # SSH key pair
  subnet_id                   = aws_subnet.public.id      # Đặt trong subnet public
  vpc_security_group_ids      = [aws_security_group.k3s.id] # Gắn security group
  associate_public_ip_address = true                      # Gán public IP

  root_block_device {
    volume_size = var.root_volume_size # Dung lượng ổ đĩa
    volume_type = "gp3"                # Loại EBS
  }

  tags = {
    Name = "${var.project_name}-vps${count.index + 1}"       # Tên VPS
    Role = count.index == 0 ? "k3s-master" : "k3s-worker"    # vps1 master, còn lại worker
  }
}

resource "local_file" "ansible_inventory" {
  content = templatefile("${path.module}/templates/inventory.ini.tftpl", {
    master_public_ip  = aws_instance.k3s[0].public_ip              # Public IP master
    master_private_ip = aws_instance.k3s[0].private_ip             # Private IP master
    worker_public_ips = slice(aws_instance.k3s[*].public_ip, 1, 3) # Public IP worker
  })
  filename = "${path.module}/../ansible/inventory/aws.ini" # File inventory cho Ansible
}
