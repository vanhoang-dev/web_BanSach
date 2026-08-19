output "master_public_ip" {
  value = aws_instance.k3s[0].public_ip
}

output "worker_public_ips" {
  value = slice(aws_instance.k3s[*].public_ip, 1, 3)
}

output "ansible_inventory" {
  value = abspath("${path.module}/../ansible/inventory/aws.ini")
}
