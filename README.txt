MiAgenda README

En este proyecto se ha implementado una agenda telefonica. Tiene dos Activities. La Activity principal con los contactos añadidos, contador, ultimo contacto
añadido y botones para ordenar, añadir o filtrar los contactos. La segunda Activity es un formulario para añadir los contactos con los botones de Aceptar y 
Cancelar.

Funcionalidades:
Entre las funcionalidades de la agenda encontramos:
    - Agregar contactos.
    - Mostrar los contactos en un RecyclerView.
    - Guardar en persistencia el ultimo contactoa añadido.
    - Contador de los contactos totales.
    - Abrir el marcador con un Intent implicito.
    - Eliminar Contacto.
    - Modificar Contacto.
    - Enviar Email si el contacto tiene agregado uno.
    - Ordenar por orden alfabético.
    - Filtrar por favoritos.

Decisiones de Diseño:
    - El MainActivity está compuesto por un ConstraintLayout y dividido por dos LinearLayout. Fué la decisión que tomé ante un primer acercamiento ante los xml 
    para que estuvierta todo alineado.
    - En un principio todos los contactos del RecylcerView iban a tener un boton para poder modificar su estado de favorito de manera inmediata. Lo descarté dado
    que el usuario podria pulsar cualquiera de estos botones sin querer.
    - La practica exige un onSaveInstanceState para guardar el estado de los interruptores. Dado que daba problemas y el estado se guardaba por si mismo decidí 
    quitarlo.

Dificultades:
    - Tardé mucho tiempo en acostumbrarme al diseño de las interfaces con xml. Lo solucioné buscando en Internet la sintaxis de lo que necesitaba y como se
    comportaba la vista con esa funcion.
    - No sabia como crear el RecyclerView ni como rellenarlo con contactos por lo que recurrí a herramientas de IA para poder implementarlo

Mejoras:
    - Persistencia de datos dado que actualmente los Contactos se eliminan al cerrar la aplicación.
    - Estructura de codigo. Al ser una primera practica el codigo es funcional pero empaquetado todo dentro de la propias activites. Haria una clase unicamente
    de logica para estructurar mejor las funcionalidadesd de las Views.