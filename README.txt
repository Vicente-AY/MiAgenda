MiAgenda 2.0 README

En este proyecto se ha mejorado MiAgenda añadiendole una pantalla de ajustes generales y una vista de detalles de los contactos. Se ha utilizado SQLite para la
persistencia de los datos dado que, al tratarse de una aplicacion de gestion de contactos podemos obtener los datos de manera inmediata sin dependencia de 
internet.

Decisiones de Diseño:
    - El MainActivity se le ha añadido un boton para acceder a AjustesActivity. El boton de ordenar alfabéticamente se ha eliminado dado que ya no respondia ante
    ningún metodo.
    - AjustesActivity tiene el contador por categoria. En un principio iba a ir dentro de MainActivity pero lo descarté para evitar saturar la actividad con 
    información.

Dificultades:
    - El cambio de ContactoRepository a RepositoryProvider junto a las diferentes clases para que la base de datos funcionara. El agente implementado
    en Android Studio ayudó a entenderlas
    - Las llamadas RepositoryProvider, con unos ejemplos encontrados en internet pude entender como funcionaba y replicarlas.

Mejoras:
    - Imagen para el contacto.
    - Poder añadir mas de un numero de telefono y email para cada contacto.
    - Añadido de Categoria manual para no depender de las añadidas por defecto.