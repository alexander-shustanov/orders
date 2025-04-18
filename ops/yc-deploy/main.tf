terraform {
    required_providers {
        yandex = {
            source = "yandex-cloud/yandex"
        }
    }

    backend "s3" {
        endpoints = {
            s3 = "https://storage.yandexcloud.net"
        }
        bucket = "amplicode-tests-tf-state"
        region = "ru-central1"
        key    = "orders/orders.tfstate"

        skip_region_validation      = true
        skip_credentials_validation = true
        skip_requesting_account_id  = true
        skip_s3_checksum            = true

    }
}

provider "yandex" {
    folder_id = var.folder_id
    service_account_key_file = var.service_account_key_file
}

data "yandex_vpc_network" "default" {
    name = "default"
}

data "yandex_vpc_subnet" "default-ru-central1-d" {
    name = "default-ru-central1-d"
}
