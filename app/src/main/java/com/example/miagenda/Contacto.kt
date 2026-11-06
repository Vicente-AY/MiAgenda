package com.example.miagenda

data class Contacto(var id: String,
                    var name: String,
                    var phone: String,
                    var email: String?,
                    var favorite: Boolean = false,
                    var category: String,
                    var notes: String?
)