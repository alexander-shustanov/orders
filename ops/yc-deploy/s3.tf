resource "yandex_iam_service_account" "storage-editor" {
    folder_id = var.folder_id
    name      = "storage-editor"
}

resource "yandex_iam_service_account" "storage-admin" {
    folder_id = var.folder_id
    name      = "storage-admin"
}

resource "yandex_resourcemanager_folder_iam_member" "storage-editor" {
    folder_id = var.folder_id
    role      = "storage.editor"
    member    = "serviceAccount:${yandex_iam_service_account.storage-editor.id}"
}

resource "yandex_resourcemanager_folder_iam_member" "storage-admin" {
    folder_id = var.folder_id
    role      = "storage.admin"
    member    = "serviceAccount:${yandex_iam_service_account.storage-admin.id}"
}

resource "yandex_iam_service_account_static_access_key" "storage-editor-static-key" {
    depends_on = [yandex_resourcemanager_folder_iam_member.storage-editor]
    service_account_id = yandex_iam_service_account.storage-editor.id
    description        = "static access key for object storage"
}

resource "yandex_iam_service_account_static_access_key" "storage-admin-static-key" {
    depends_on = [yandex_resourcemanager_folder_iam_member.storage-admin]
    service_account_id = yandex_iam_service_account.storage-admin.id
    description        = "static access key for object storage"
}

resource "yandex_storage_bucket" "products" {
    folder_id = var.folder_id

    access_key = yandex_iam_service_account_static_access_key.storage-admin-static-key.access_key
    secret_key = yandex_iam_service_account_static_access_key.storage-admin-static-key.secret_key

    bucket = "orders-products"

    anonymous_access_flags {
        read = true
    }

    grant {
        id = yandex_iam_service_account.storage-editor.id
        type = "CanonicalUser"
        permissions = ["READ", "WRITE"]
    }
}
