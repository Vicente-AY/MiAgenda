package com.example.miagenda.model

data class Contacto(var id: String = "",
                    var name: String,
                    var phone: String,
                    var email: String? = null,
                    var favorite: Boolean = false,
                    var category: String = "Otros",
                    var notes: String? = null,
                    var birthdate: String? = null
)