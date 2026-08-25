output "master_public_ip" {
  value = aws_instance.k3s[0].public_ip # Public IP của vps1 master
}

output "worker_public_ips" {
  value = slice(aws_instance.k3s[*].public_ip, 1, 3) # Public IP của vps2/vps3 worker
}

output "ansible_inventory" {
  value = abspath("${path.module}/../ansible/inventory/aws.ini") # File inventory Terraform sinh cho Ansible
}
