# ClubSync-Manager

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-323330?style=for-the-badge&logo=javascript&logoColor=F7DF1E)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)

## 🚀 El Origen del Proyecto: De la Facultad al Mundo Real

**ClubSync** nació originalmente como un trabajo práctico académico para la carrera de Ingeniería de Sistemas. En su primera versión, el sistema fue modelado y desarrollado en **Smalltalk**. 

Esa etapa inicial fue fundamental para el aprendizaje sobre una arquitectura basada puramente en el paradigma de la Programación Orientada a Objetos (POO), modelando el dominio de un club, sus socios y las transacciones financieras con un alto nivel de abstracción.

Sin embargo, al ver el potencial práctico del sistema, decidí llevar el proyecto más allá del entorno académico y transformarlo en una aplicación web moderna, escalable y lista para producción. 

### 🔄 La Evolución Tecnológica (Migración)
Para dar este salto de calidad, refactoricé y migré toda la lógica de negocio hacia un stack tecnológico demandado por la industria actual:
* **Del entorno local a la Web:** El backend fue reescrito completamente en **Java** utilizando el framework **Spring Boot**, exponiendo la lógica a través de una API REST.
* **Persistencia Relacional:** Se reemplazó el manejo de objetos en memoria por una base de datos relacional (MySQL) gestionada mediante **Spring Data JPA / Hibernate**.
* **Interfaz y Experiencia de Usuario (UX/UI):** Se desarrolló un frontend interactivo desde cero utilizando **JavaScript**, **Fetch API** para el asincronismo y **Bootstrap 5** para un diseño responsivo, emulando la experiencia de los sistemas ERP modernos.
* **Nuevas Features:** Aprovechando el nuevo stack, escalé el sistema integrando herramientas del mundo real, como una pasarela de pagos (Mercado Pago), generación automática de comprobantes en PDF (iText) y un Asistente Virtual híbrido.

Este proyecto representa mi transición desde la asimilación de conceptos teóricos profundos hacia la construcción de software con estándares y arquitecturas empresariales.


## ClubSync: Gestion de Club

ClubSync es una plataforma web integral diseñada para la administración de clubes y asociaciones. Permite gestionar desde el padrón de socios hasta el cronograma de actividades, reservas de instalaciones y control de tesorería con integración de pagos QR.

## ✨ Funcionalidades Principales

* **👥 Gestión de Padrón:** Administración completa de Socios y Personas (no socios). Perfiles detallados, historial de asociación y estados (Activo/Inactivo/Moroso).
  
* **🏟️ Control de Instalaciones:** ABM de espacios físicos (canchas, piletas, salones) con control de capacidad, dimensiones y estado de mantenimiento.
  
* **📅 Actividades y Eventos:** Programación de actividades recurrentes y eventos especiales. Asignación de profesores/responsables, control de cupos y grilla de horarios.
  
* **💳 Cobranzas y Mercado Pago:** Sistema de facturación integrado. Generación automática de códigos QR para pagos mediante Mercado Pago, con un polling en tiempo real que verifica la acreditación del pago y actualiza la UI sin recargar la página.

* **🤖 Asistente Virtual Inteligente (IA Integrada):** Inicialmente fue hecho consumiendo la API de una Inteligencia Artificial y diseñado con arquitectura **RAG** y **Function Calling**. Y, si bien esta integración **sigue completamente funcional y disponible en el sistema** para consultas de lenguaje natural, el asistente evolucionó hacia una **arquitectura híbrida** donde se incorporo un **Modelo Deterministico**. Gracias este cambio, se logro reducir la dependencia API de terceros y sus tokens, tambien se garantizo la precision en las respuestas del ChatBot al reducir las alucinaciones a 0, ademas
  
* **📊 Web Responsiva:** Interfaz moderna, totalmente adaptable a dispositivos móviles, garantizando una buena experiencia de usuario (UX).



## 🏗️ Arquitectura y Tecnologías

El proyecto sigue una arquitectura de capas, separando claramente las responsabilidades del backend y el frontend para facilitar su mantenimiento y escalabilidad.

**Backend:**
* **Framework:** Spring Boot (Java)
* **Inteligencia Artificial:** Spring AI + Groq API.
* **Persistencia:** JPA / Hibernate
* **Base de Datos:** MySQL (con Data Seeder para inicialización de entornos).
* **API:** REST para la comunicación fluida con el cliente.

**Frontend:**
* **Core:** JS, HTML5, CSS3.
* **Diseño:** Bootstrap 5.



## 🚀 Instalación y Uso local

Si querés clonar este repositorio y levantar el entorno de desarrollo, seguí estos pasos:

1. Cloná el repositorio:
   ```bash
   git clone [https://github.com/tu-usuario/proclub-manager.git](https://github.com/tu-usuario/proclub-manager.git)

2. Configurá tu base de datos en src/main/resources/application.properties:
    ```bash
    # Base de Datos
    spring.datasource.url=jdbc:mysql://localhost:3306/tu_base_de_datos
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    
    # Configuración de Spring AI (Groq)
    spring.ai.openai.api-key=TU_API_KEY_AQUI
    spring.ai.openai.base-url=[https://api.groq.com/openai/v1](https://api.groq.com/openai/v1)
    spring.ai.openai.chat.options.model=openai/gpt-oss-120b

    # Configuracion de Mercado Pago API
    mercadopago.access-token=TU_API_KEY_AQUI

3. Ejecutá la aplicación desde tu IDE
    ```bash
    mvn spring-boot:run

4. Ingresá a http://localhost:8080 en tu navegador.

(Nota: El proyecto incluye un DataLoader que "siembra" la base de datos con registros de prueba si se ejecuta con la propiedad ddl-auto=create. Importante volver a poner update luego de usar el DataLoader).

Desarrollado por Julian Carmona (www.linkedin.com/in/julián-carmona-497b55286)
