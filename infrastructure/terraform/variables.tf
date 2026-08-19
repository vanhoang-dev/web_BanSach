variable "aws_region" {
  description = "AWS region to create the 3 VPS/EC2 instances."
  type        = string
  default     = "ap-southeast-1"
}

variable "project_name" {
  type    = string
  default = "web-bansach"
}

variable "instance_type" {
  description = "Use at least 2GB RAM per node for k3s demo workloads."
  type        = string
  default     = "t3.small"
}

variable "key_name" {
  description = "Existing AWS EC2 key pair name."
  type        = string
}

variable "ssh_public_key_path" {
  description = "Local public key path used by Ansible SSH."
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "allowed_ssh_cidr" {
  description = "Your public IP CIDR for SSH, for example 203.0.113.10/32."
  type        = string
}

variable "root_volume_size" {
  type    = number
  default = 30
}
