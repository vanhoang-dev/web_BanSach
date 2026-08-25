terraform {
  required_version = ">= 1.6.0" # Phiên bản Terraform tối thiểu

  required_providers {
    aws = {
      source  = "hashicorp/aws" # Provider quản lý AWS
      version = "~> 5.0"        # Phiên bản provider AWS
    }
    local = {
      source  = "hashicorp/local" # Provider tạo file local
      version = "~> 2.5"          # Phiên bản provider local
    }
  }
}
