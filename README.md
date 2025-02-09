# Juego de Blackjack - Documentación

Este documento proporciona un README, documentación técnica y un manual de usuario para el código Java del juego de Blackjack.

## Tabla de Contenido
1.  [README](#readme)
2.  [Documentación Técnica](#documentación-técnica)
3.  [Manual de Usuario](#manual-de-usuario)

## README

### Título del Proyecto: Juego Simple de Blackjack (Línea de Comandos)

### Descripción

Este proyecto implementa un juego básico de Blackjack basado en línea de comandos en Java. Presenta una arquitectura cliente-servidor que permite que dos o más jugadores se conecten a un servidor y jueguen Blackjack contra un crupier (simulado por la lógica del servidor).

### Funcionalidad

*   **Soporte Multijugador:** Permite que múltiples jugadores se conecten y jueguen en la misma sesión de juego. El juego comienza cuando al menos dos jugadores se conectan.
*   **Arquitectura Cliente-Servidor:** Separa la lógica del juego (servidor) de la interacción del jugador (cliente).
*   **Reglas Básicas de Blackjack:** Implementa las reglas centrales del Blackjack incluyendo:
    *   Repartir cartas a los jugadores y al crupier.
    *   Acciones del jugador: "pedir" y "plantarse".
    *   Turno y reglas del crupier (el crupier pide carta hasta llegar al menos a 17).
    *   Cálculo de los valores de las manos (incluyendo el As como 1 u 11).
    *   Determinación de ganadores y perdedores según las reglas del Blackjack.
*   **Interfaz de Línea de Comandos:** Los jugadores interactúan con el juego a través de comandos de texto en la línea de comandos.
*   **Baraja de Cartas Simple:** Utiliza una baraja estándar de 52 cartas (representada solo por rangos de cartas, los palos no se implementan por simplicidad).

### Cómo Ejecutar

1.  **Ejecutar el Servidor:**
    *   Abre una terminal y navega al directorio que contiene los archivos `.class` compilados (probablemente el directorio raíz si compilaste como se sugirió anteriormente).
    *   Ejecuta el servidor usando: `java net.salesianos.server.BlackjackServer`
    *   El servidor se iniciará e imprimirá "Servidor Blackjack iniciado en el puerto 8082".
2.  **Ejecutar el/los Cliente(s):**
    *   Abre una o más terminales nuevas para cada jugador.
    *   Navega al directorio que contiene los archivos `.class` compilados.
    *   Ejecuta el cliente usando: `java net.salesianos.client.PlayerClient`
    *   El cliente te preguntará "Bienvenido al Blackjack!" e "Introduce tu nombre:". Introduce un nombre de jugador y presiona Enter.
    *   Repite el paso 3 para cada jugador que quiera unirse al juego.
3.  **Empezar el Juego:**
    *   Una vez que al menos dos jugadores se hayan conectado, el servidor iniciará el juego.
    *   Sigue las instrucciones que se muestran en las terminales del cliente para jugar Blackjack. Se te pedirá que "pedir" o "plantarse".
    *   Los mensajes del juego y los resultados se mostrarán en las terminales del cliente.

### Dependencias

*   Java Development Kit (JDK) 8 o superior.

---

## Documentación Técnica

### Estructura del Proyecto

El proyecto está organizado en los siguientes paquetes:

*   **`net.salesianos.client`**: Contiene las clases relacionadas con la aplicación cliente.
    *   `PlayerClient.java`: La clase principal para la aplicación cliente de Blackjack. Maneja la entrada del usuario, la conexión de red al servidor y la comunicación.
    *   `net.salesianos.client.theards`
        *   `ServerListener.java`: Un hilo (thread) que se ejecuta en el cliente para escuchar continuamente los mensajes del servidor y mostrarlos al usuario.
*   **`net.salesianos.decks`**: Contiene las clases relacionadas con las barajas de cartas.
    *   `Deck.java`: Representa una baraja de cartas. Proporciona funcionalidad para crear una baraja, barajarla y robar cartas.
*   **`net.salesianos.server`**: Contiene las clases relacionadas con la aplicación servidor.
    *   `BlackjackServer.java`: La clase principal para la aplicación servidor de Blackjack. Gestiona la lógica del juego, las conexiones de los clientes y el flujo del juego.
    *   `net.salesianos.server.threads`
        *   `ClientHandler.java`: Un hilo (thread) que se ejecuta en el servidor para cada cliente conectado. Maneja la comunicación con un cliente específico, procesa las acciones del jugador y actualiza el estado del juego.
*   **`net.salesianos.utils`**: Contiene clases de utilidad y constantes.
    *   `Constants.java`: Define valores constantes utilizados en todo el proyecto, como el número de puerto del servidor (`SEVER_PORT`).

### Descripciones de Clases y Funcionalidad

#### `net.salesianos.client.PlayerClient`

*   **Propósito:** Esta clase representa la aplicación cliente que los jugadores utilizan para conectarse al servidor de Blackjack y jugar.
*   **Funcionalidad:**
    *   Inicializa un `Scanner` para leer la entrada del usuario desde la línea de comandos.
    *   Solicita al usuario que introduzca su nombre.
    *   Establece una conexión `Socket` con el servidor que se ejecuta en `localhost` en el puerto definido en `Constants.SEVER_PORT`.
    *   Crea `DataOutputStream` y `DataInputStream` para manejar la comunicación con el servidor a través del socket.
    *   Envía el nombre del jugador al servidor al conectarse.
    *   Inicia un hilo `ServerListener` para manejar los mensajes entrantes del servidor.
    *   Entra en un bucle para continuamente:
        *   Solicitar la entrada del usuario (acción del jugador: "pedir" o "plantarse").
        *   Enviar la entrada del usuario como un mensaje al servidor.
        *   Continuar hasta que el hilo `ServerListener` indique que el juego ha terminado (`isRunning()` devuelve `false`).
    *   Cierra la conexión del socket cuando el juego termina o se produce un error.
*   **Métodos Clave:**
    *   `main(String[] args)`: El punto de entrada de la aplicación cliente, configura la conexión, los flujos de entrada/salida, inicia el `ServerListener` y maneja la interacción del usuario.

#### `net.salesianos.client.theards.ServerListener`

*   **Propósito:** Esta clase extiende `Thread` y es responsable de escuchar continuamente los mensajes del servidor de Blackjack en el lado del cliente y mostrarlos al usuario.
*   **Funcionalidad:**
    *   Recibe un `DataInputStream` conectado al socket del servidor en su constructor.
    *   Implementa el método `run()` de la clase `Thread` para ejecutarse en un hilo separado.
    *   Dentro del método `run()`, entra en un bucle `while` que continúa mientras `running` sea `true`.
    *   En cada iteración, intenta leer un mensaje del servidor utilizando `inputStream.readUTF()`.
    *   Imprime el mensaje del servidor recibido en la consola, precedido por "Servidor dice: ".
    *   Comprueba si el mensaje recibido indica que el juego ha terminado (contiene "¡Juego terminado!", "¡Has ganado!", o "¡Has perdido!"). Si es así, establece `running` a `false` para terminar el bucle y el hilo.
    *   Maneja `EOFException` (el servidor cierra la conexión) y `IOException` general (error al recibir el mensaje) imprimiendo mensajes de error y estableciendo `running` a `false`.
    *   En el bloque `finally`, intenta cerrar el `inputStream` si el hilo ya no se está ejecutando.
*   **Métodos Clave:**
    *   `run()`: El método principal del hilo, responsable de escuchar los mensajes del servidor y actualizar la consola del cliente.
    *   `isRunning()`: Devuelve el estado actual del hilo listener (`true` si se está ejecutando, `false` si se ha detenido).

#### `net.salesianos.decks.Deck`

*   **Propósito:** Esta clase representa una baraja estándar de cartas de juego utilizada en Blackjack.
*   **Funcionalidad:**
    *   Inicializa una baraja de cartas en el constructor. Por simplicidad, solo utiliza rangos de cartas ("A", "2", "3", ..., "K") y no incluye palos.
    *   Baraja la baraja utilizando `Collections.shuffle()` cuando se crea y cuando se llama al método `shuffle()`.
    *   Proporciona un método `drawCard()` para eliminar y devolver la carta superior de la baraja.
*   **Métodos Clave:**
    *   `Deck()`: Constructor que crea y baraja una nueva baraja de cartas.
    *   `shuffle()`: Baraja la baraja de cartas existente.
    *   `drawCard()`: Roba y devuelve la carta superior de la baraja (la elimina de la lista de la baraja).

#### `net.salesianos.server.BlackjackServer`

*   **Propósito:** Esta clase representa la aplicación servidor principal para el juego de Blackjack. Gestiona la lógica del juego, las conexiones de los clientes y el flujo del juego.
*   **Funcionalidad:**
    *   Mantiene una `List` de hilos `ClientHandler` (`players`) para gestionar los jugadores conectados.
    *   Crea una única instancia de `Deck` para el juego.
    *   Realiza un seguimiento de la mano del crupier (`dealerHand`) y del número de jugadores que han terminado su turno (`playersFinished`).
    *   Inicia un `ServerSocket` para escuchar las conexiones de clientes entrantes en el puerto definido en `Constants.SEVER_PORT`.
    *   En un bucle `while(true)`, continuamente:
        *   Acepta nuevas conexiones de clientes utilizando `serverSocket.accept()`.
        *   Crea un nuevo hilo `ClientHandler` para cada cliente conectado.
        *   Añade el nuevo `ClientHandler` a la lista `players`.
        *   Envía las reglas del juego al jugador recién conectado utilizando `sendGameRules()`.
        *   Inicia el hilo `ClientHandler`.
        *   Comprueba si hay al menos dos jugadores conectados (`players.size() >= 2`). Si es así, inicia un nuevo juego utilizando `startGame()`.
    *   Proporciona métodos estáticos para:
        *   `broadcast(String message)`: Envía un mensaje a todos los jugadores conectados actualmente.
        *   `startGame()`: Inicializa una nueva ronda de Blackjack, baraja la baraja, reparte las cartas iniciales a los jugadores y al crupier.
        *   `drawCardFromDeck()`: Roba una carta de la baraja del juego.
        *   `playerFinishedTurn()`: Incrementa el contador `playersFinished` e inicia el turno del crupier (`dealerTurn()`) si todos los jugadores han terminado.
        *   `dealerTurn()`: Ejecuta el turno del crupier de acuerdo con las reglas del Blackjack (el crupier pide carta hasta que el valor de la mano sea 17 o más), determina los ganadores, termina el juego y cierra las conexiones de los jugadores.
        *   `calculateHandValue(List<String> hand)`: Calcula el valor de una mano de Blackjack, manejando los Ases como 1 u 11.
*   **Métodos Clave:**
    *   `main(String[] args)`: El punto de entrada de la aplicación servidor, configura el `ServerSocket`, escucha las conexiones y gestiona el ciclo de vida del juego.
    *   `broadcast(String message)`: Envía un mensaje a todos los jugadores conectados.
    *   `startGame()`: Inicia una nueva ronda del juego.
    *   `dealerTurn()`: Maneja el turno del crupier y determina los resultados del juego.
    *   `calculateHandValue(List<String> hand)`: Calcula el valor de una mano.

#### `net.salesianos.server.threads.ClientHandler`

*   **Propósito:** Esta clase extiende `Thread` y es responsable de manejar la comunicación y las acciones del juego para un único cliente conectado en el lado del servidor.
*   **Funcionalidad:**
    *   Recibe un `Socket` que representa la conexión del cliente en su constructor.
    *   Crea `DataInputStream` y `DataOutputStream` para manejar la comunicación con el cliente a través del socket.
    *   Lee el nombre del jugador del cliente al conectarse.
    *   Difunde un mensaje a todos los jugadores anunciando que el nuevo jugador se ha unido al juego.
    *   Implementa el método `run()` de la clase `Thread` para ejecutarse en un hilo separado para cada cliente.
    *   Dentro del método `run()`, entra en un bucle `while(running)` para continuamente:
        *   Leer las acciones del jugador desde el cliente utilizando `input.readUTF()` (las acciones esperadas son "pedir" o "plantarse").
        *   Procesar la acción del jugador:
            *   Si "pedir": Roba una carta de la baraja, se la da al jugador y comprueba si el jugador se ha pasado (valor de la mano > 21). Si se ha pasado, envía un mensaje de "pasado", cierra la conexión del jugador y llama a `BlackjackServer.playerFinishedTurn()`.
            *   Si "plantarse": Envía un mensaje de "plantarse" y llama a `BlackjackServer.playerFinishedTurn()`.
        *   Difunde un mensaje a todos los jugadores indicando la acción del jugador.
    *   Maneja `IOException` (error de comunicación) imprimiendo un mensaje de error, cerrando la conexión y potencialmente indicando la desconexión del jugador al servidor.
    *   Proporciona métodos para:
        *   `sendMessage(String message)`: Envía un mensaje al cliente conectado.
        *   `giveCard(String card)`: Añade una carta a la mano del jugador y envía un mensaje al cliente indicando la carta recibida.
        *   `showHand()`: Envía un mensaje al cliente mostrando su mano actual.
        *   `getHand()`: Devuelve la mano actual del jugador.
        *   `isConnected()`: Comprueba si la conexión del socket sigue abierta.
        *   `closeConnection()`: Cierra la conexión del socket y establece `running` a `false`.
*   **Métodos Clave:**
    *   `run()`: El método principal del hilo, maneja la comunicación con el cliente y procesa las acciones del jugador.
    *   `sendMessage(String message)`: Envía un mensaje al cliente.
    *   `giveCard(String card)`: Da una carta al jugador.

#### `net.salesianos.utils.Constants`

*   **Propósito:** Esta clase define valores constantes utilizados en el proyecto.
*   **Funcionalidad:**
    *   `SEVER_PORT`: Una variable `public static final int` que define el número de puerto en el que el servidor escuchará y al que se conectarán los clientes (establecido en 8082).

### Flujo del Juego

1.  **Inicio del Servidor:** Se inicia la aplicación `BlackjackServer`. Inicializa la baraja, configura el `ServerSocket` y comienza a escuchar las conexiones de los clientes.
2.  **Conexión del Cliente:** Los jugadores inician la aplicación `PlayerClient`. Cada cliente se conecta al servidor, proporcionando su nombre.
3.  **Unión de Jugadores:** Cuando un cliente se conecta, el servidor crea un hilo `ClientHandler` para ese cliente y lo añade a la lista de jugadores. Se difunde un mensaje a todos los jugadores informando de que un nuevo jugador se ha unido.
4.  **Inicio del Juego (Mínimo 2 Jugadores):** Una vez que al menos dos jugadores se han conectado, el `BlackjackServer` inicia el juego.
    *   Se baraja la baraja.
    *   Se inicializa la mano del crupier con dos cartas (la primera carta se revela a los jugadores).
    *   Se reparten dos cartas iniciales a cada jugador.
    *   Se pide a cada jugador que haga un movimiento ("pedir" o "plantarse").
5.  **Turnos de los Jugadores:** Cada jugador, por turno, interactúa con su aplicación cliente para tomar decisiones:
    *   **"pedir":** El cliente envía "pedir" al servidor. El servidor roba una carta de la baraja, se la da al jugador y comprueba si el jugador se ha pasado. Se muestra la mano actualizada al jugador.
    *   **"plantarse":** El cliente envía "plantarse" al servidor. El servidor registra que el jugador se ha plantado y espera a otros jugadores.
6.  **Turno del Crupier:** Después de que todos los jugadores se hayan pasado o hayan elegido plantarse, el servidor inicia el turno del crupier.
    *   El crupier sigue robando cartas hasta que el valor de su mano sea 17 o más.
    *   Se revela la mano completa del crupier a todos los jugadores.
7.  **Determinar Ganadores:** El servidor compara el valor de la mano de cada jugador con el valor de la mano del crupier para determinar el resultado para cada jugador.
    *   Los jugadores que se han pasado pierden automáticamente.
    *   Los jugadores que tienen un valor de mano más cercano a 21 que el crupier (sin pasarse) ganan.
    *   Los jugadores que tienen un valor de mano menor o igual que el valor de la mano del crupier (si el crupier no se ha pasado) pierden.
8.  **Fin del Juego:** El servidor difunde los resultados del juego ("¡Has ganado!", "¡Has perdido!") a cada jugador y un mensaje de "¡Juego terminado!" a todos los jugadores. Se cierran las conexiones de los jugadores. El servidor espera entonces nuevas conexiones de clientes para iniciar un nuevo juego.

### Estructuras de Datos

*   **`List<ClientHandler> players` (en `BlackjackServer`):** Almacena una lista de hilos `ClientHandler`, que representan a los jugadores conectados en el juego.
*   **`Deck deck` (en `BlackjackServer`):** Representa la baraja de cartas utilizada en el juego.
*   **`List<String> dealerHand` (en `BlackjackServer`):** Almacena las cartas en la mano del crupier.
*   **`List<String> hand` (en `ClientHandler`):** Almacena las cartas en la mano de un jugador.

### Red

*   **Sockets:** El juego utiliza Sockets de Java para la comunicación de red entre el servidor y los clientes.
    *   `ServerSocket` (en `BlackjackServer`): Utilizado por el servidor para escuchar y aceptar las conexiones de clientes entrantes.
    *   `Socket` (en `PlayerClient` y `ClientHandler`): Utilizado por los clientes para conectarse al servidor y por el servidor para comunicarse con cada cliente.
*   **Flujos de Datos:** `DataInputStream` y `DataOutputStream` se utilizan para enviar y recibir datos a través de las conexiones de socket. Estos flujos permiten enviar tipos de datos primitivos y cadenas codificadas en UTF-8, lo que simplifica la comunicación.

### Hilos (Threading)

*   **Se utiliza multihilo para la concurrencia:**
    *   **`ServerListener` (hilo del lado del cliente):** Permite al cliente recibir y mostrar continuamente los mensajes del servidor sin bloquear la entrada del usuario.
    *   **`ClientHandler` (hilo del lado del servidor):** Permite al servidor manejar múltiples conexiones de clientes de forma concurrente. Cada cliente es gestionado por su propio hilo, lo que permite el juego simultáneo para varios jugadores.

---

## Manual de Usuario

### Juego de Blackjack - Manual de Usuario

¡Bienvenido al juego de Blackjack! Este manual te guiará sobre cómo jugar.

### Introducción

Blackjack es un juego de cartas popular donde el objetivo es tener un valor de mano lo más cercano posible a 21 sin pasarse (reventar), y superar la mano del crupier.

### Cómo Jugar

#### 1. Iniciar el Juego

*   **Ejecutar el Servidor:** Primero, alguien necesita iniciar la aplicación servidor de Blackjack. Deberán seguir las instrucciones de "Cómo Ejecutar" en el [README](#cómo-ejecutar) para iniciar el servidor. Hazles saber que el servidor está en funcionamiento.
*   **Ejecutar el Cliente:** Cada jugador necesita iniciar la aplicación cliente de Blackjack en su propio ordenador. Sigue las instrucciones de "Cómo Ejecutar" en el [README](#cómo-ejecutar) para iniciar el cliente.

#### 2. Unirse al Juego

*   **Introduce tu Nombre:** Cuando ejecutes el cliente, verás el mensaje:
    ```
    Bienvenido al Blackjack!
    Introduce tu nombre:
    ```
    Escribe tu nombre y pulsa Enter. Este nombre se mostrará a otros jugadores en el juego.
*   **Espera a que Comience el Juego:** El juego comenzará automáticamente una vez que al menos dos jugadores se hayan unido al servidor. Verás mensajes del servidor en la ventana de tu cliente.

#### 3. Jugabilidad

*   **Reglas del Juego Mostradas:** Al principio, las reglas del juego se mostrarán en la ventana de tu cliente. Por favor, léelas para entender las reglas básicas del Blackjack.
*   **Tu Turno:** Una vez que comience el juego, recibirás tus dos cartas iniciales, y se revelará la primera carta del crupier. A continuación, se te pedirá que tomes una decisión en la ventana de tu cliente, mostrando tu nombre seguido de "-> ".
*   **Acciones Disponibles:** Tienes dos acciones principales para elegir durante tu turno:
    *   **`pedir` (Pedir Carta):** Escribe `pedir` y pulsa Enter para solicitar otra carta de la baraja. Esto aumentará el valor de tu mano y te acercará a 21, ¡pero ten cuidado de no pasarte!
    *   **`plantarse` (Plantarse):** Escribe `plantarse` y pulsa Enter para mantener tu mano actual y terminar tu turno. Si estás satisfecho con tu mano y crees que está lo suficientemente cerca de 21 para vencer al crupier, debes elegir plantarte.
*   **Mensajes del Juego:** Presta atención a los mensajes que se muestran en la ventana de tu cliente. El servidor enviará mensajes sobre:
    *   Las cartas que recibes.
    *   Tu mano actual.
    *   La primera carta del crupier.
    *   La mano completa del crupier (al final de la ronda).
    *   Los resultados del juego (si ganas o pierdes).
    *   El fin del juego.

#### 4. Objetivo y Reglas del Juego (Resumen)

*   **Objetivo:** Consigue que el valor de tu mano sea lo más cercano posible a 21 sin superar 21, y tener un valor de mano mayor que la mano del crupier.
*   **Valores de las Cartas:**
    *   Las cartas numéricas (2-10) valen su valor nominal.
    *   La Jota (J), la Reina (Q) y el Rey (K) valen 10 cada una.
    *   El As (A) puede valer 1 u 11, lo que sea mejor para tu mano (para evitar pasarse).
*   **Reventar (Bust):** Si el valor de tu mano supera 21, "revientas" y pierdes automáticamente, independientemente de la mano del crupier.
*   **Reglas del Crupier:** El crupier debe "pedir" (tomar otra carta) si el valor de su mano es 16 o menos y debe "plantarse" (dejar de tomar cartas) si el valor de su mano es 17 o más.
*   **Ganar:** Ganas si:
    *   El valor de tu mano está más cerca de 21 que el valor de la mano del crupier (sin pasarte).
    *   El crupier revienta (supera 21) y tú no.
*   **Perder:** Pierdes si:
    *   Tú revientas (superas 21).
    *   El valor de tu mano es menor o igual que el valor de la mano del crupier (y el crupier no revienta).

#### 5. Finalizar el Juego

*   La ronda de juego termina después de que todos los jugadores hayan terminado sus turnos y el crupier haya completado su turno.
*   El servidor mostrará los resultados del juego y un mensaje de "¡Juego terminado!".
*   Para volver a jugar, el servidor debe estar en funcionamiento y los jugadores pueden volver a conectar sus clientes para iniciar un nuevo juego cuando se unan suficientes jugadores.

### Solución de Problemas

*   **"Error al conectar con el servidor."**: Si ves este mensaje al ejecutar el cliente, significa que el cliente no pudo conectarse al servidor.
    *   Asegúrate de que la aplicación servidor se esté ejecutando correctamente **primero**.
    *   Asegúrate de que tanto el servidor como el cliente se estén ejecutando en la misma red o que el cliente esté configurado para conectarse a la dirección correcta del servidor (en este caso, "localhost" significa que el servidor se está ejecutando en la misma máquina que el cliente).
    *   Comprueba si hay algún problema de firewall que bloquee la conexión en el puerto 8082.
*   **El Juego se Congela o Deja de Responder:** Si el juego parece congelarse o dejar de responder, podría deberse a problemas de red o a un error en el servidor. En esta versión simple de línea de comandos, es posible que tengas que reiniciar tanto la aplicación cliente como la del servidor para volver a jugar.

¡Disfruta jugando al Blackjack!