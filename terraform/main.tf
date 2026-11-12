terraform {
  required_version = ">= 1.0"

  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 2.2"
    }
  }
}

# Credenciales por variables de entorno:
#   MONGODB_ATLAS_PUBLIC_KEY y MONGODB_ATLAS_PRIVATE_KEY
provider "mongodbatlas" {}

variable "org_id" {
  type        = string
  description = "ID de la organización de MongoDB Atlas"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Contraseña del usuario de la base de datos"
}

resource "mongodbatlas_project" "franchise" {
  name   = "franchise-api"
  org_id = var.org_id
}

# Cluster gratuito M0 en AWS (Atlas permite uno solo por proyecto)
resource "mongodbatlas_advanced_cluster" "franchise" {
  project_id   = mongodbatlas_project.franchise.id
  name         = "franchise-cluster"
  cluster_type = "REPLICASET"

  replication_specs = [
    {
      region_configs = [
        {
          provider_name         = "TENANT"
          backing_provider_name = "AWS"
          region_name           = "US_EAST_1"
          priority              = 7
          electable_specs = {
            instance_size = "M0"
          }
        }
      ]
    }
  ]
}

resource "mongodbatlas_database_user" "api" {
  project_id         = mongodbatlas_project.franchise.id
  username           = "franchise_api_user"
  password           = var.db_password
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = "franchise_db"
  }
}

# Acceso desde cualquier IP (necesario para Render; solo para este proyecto académico)
resource "mongodbatlas_project_ip_access_list" "anywhere" {
  project_id = mongodbatlas_project.franchise.id
  cidr_block = "0.0.0.0/0"
  comment    = "Render / proyecto academico"
}

output "connection_strings" {
  value = mongodbatlas_advanced_cluster.franchise.connection_strings
}