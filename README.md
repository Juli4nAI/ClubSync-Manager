# ClubSync-Manager

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-323330?style=for-the-badge&logo=javascript&logoColor=F7DF1E)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-131415?style=for-the-badge&logo=render&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)

## 🌐 Demo Online
🚀 **[Visitar ClubSync](https://club-sync-manager.vercel.app/)**

## 🎓 El Origen del Proyecto: De la Facultad al Mundo Real

**ClubSync** nació originalmente como un trabajo práctico académico para la carrera de Ingeniería de Sistemas. En su primera versión, el sistema fue modelado y desarrollado en **Smalltalk**. 

Esa etapa inicial fue fundamental para el aprendizaje sobre una arquitectura basada puramente en el paradigma de la Programación Orientada a Objetos (POO), modelando el dominio de un club, sus socios y las transacciones financieras con un alto nivel de abstracción.

Sin embargo, al ver el potencial práctico del sistema, decidí llevar el proyecto más allá del entorno académico y transformarlo en una aplicación web moderna, escalable y lista para producción. 

### 🔄 La Evolución Tecnológica (Migración)
Para dar este salto de calidad, refactoricé y migré toda la lógica de negocio hacia un stack tecnológico demandado por la industria actual:
* **Del entorno local a la Web:** El backend fue reescrito completamente en **Java** utilizando el framework **Spring Boot**, exponiendo la lógica a través de una API REST.
* **Persistencia Relacional:** Se reemplazó el manejo de objetos en memoria por una base de datos relacional (MySQL) gestionada mediante **Spring Data JPA / Hibernate**.
* **Interfaz y Experiencia de Usuario (UX/UI):** Se desarrolló un frontend interactivo desde cero utilizando **JavaScript**, **Fetch API** para el asincronismo y **Bootstrap 5** para un diseño responsivo, emulando la experiencia de los sistemas ERP modernos.
* **Nuevas Implementaciones:** Aprovechando el nuevo stack, escalé el sistema integrando herramientas del mundo real, como una pasarela de pagos (Mercado Pago), generación automática de comprobantes en PDF (iText) y un Asistente Virtual híbrido.

Este proyecto representa mi transición desde la asimilación de conceptos teóricos profundos hacia la construcción de software con estándares y arquitecturas empresariales.


## 📋 ClubSync: Gestión de Club

ClubSync es una plataforma web integral diseñada para la administración de clubes y asociaciones. Permite gestionar desde el padrón de socios hasta el cronograma de actividades, reservas de instalaciones y control de tesorería con integración de pagos QR.

## ✨ Funcionalidades Principales

* **👥 Gestión de Padrón:** Administración completa de Socios y Personas (no socios). Perfiles detallados, historial de asociación y estados (Activo/Inactivo/Moroso).
* **🏟️ Control de Instalaciones:** ABM de espacios físicos (canchas, piletas, salones) con control de capacidad, dimensiones y estado de mantenimiento.
* **📅 Actividades y Eventos:** Programación de actividades recurrentes y eventos especiales. Asignación de profesores/responsables, control de cupos y grilla de horarios.
* **💳 Cobranzas y Mercado Pago:** Sistema de facturación integrado. Generación automática de códigos QR para pagos mediante Mercado Pago, con un polling en tiempo real que verifica la acreditación del pago y actualiza la UI sin recargar la página.
* **🤖 Asistente Virtual Inteligente (IA Integrada):** Inicialmente fue hecho consumiendo la API de una Inteligencia Artificial y diseñado con arquitectura **RAG** y **Function Calling**. El asistente evolucionó hacia una **arquitectura híbrida** donde se incorporó un **Modelo Determinístico**. Gracias a este cambio, se logró reducir la dependencia de APIs de terceros, garantizando la precisión en las respuestas del ChatBot al reducir las alucinaciones a 0, manteniendo la opción de usar el LLM Groq disponible.
* **📊 Web Responsiva:** Interfaz moderna, totalmente adaptable a dispositivos móviles, garantizando una buena experiencia de usuario (UX).


## 🏗️ Arquitectura y Tecnologías

El proyecto sigue una arquitectura de capas, implementando un enfoque de **Monorepo** pero con un despliegue **totalmente desacoplado** (Frontend y Backend separados) para facilitar su mantenimiento y escalabilidad.

**Backend (API REST):**
* **Framework:** Spring Boot (Java 17).
* **Inteligencia Artificial:** Spring AI + Groq API.
* **Persistencia:** Spring Data JPA / Hibernate.
* **Base de Datos:** MySQL (con Data Seeder para inicialización de entornos).

**Frontend (SPA):**
* **Core:** Vanilla JavaScript (ES6+), HTML5, CSS3.
* **Diseño:** Bootstrap 5.
* **Comunicación:** Fetch API con manejo de CORS configurado en el backend.

---

## ☁️ Infraestructura, Docker y CI/CD

El sistema fue diseñado para operar en la nube utilizando prácticas de DevOps:

* **Containerización (Docker):** El backend de Spring Boot está encapsulado mediante un `Dockerfile`, lo que garantiza que la aplicación corra de manera idéntica en cualquier entorno, manejando sus propias dependencias y versión de Java.
* **Arquitectura Desacoplada en Producción:**
  * **Frontend (Vercel):** La carpeta `/frontend` se despliega automáticamente en Vercel, aprovechando su CDN global para una carga instantánea de los recursos estáticos.
  * **Backend & Base de Datos:** El contenedor Docker de Spring Boot y la instancia productiva de MySQL operan dentro de la red privada de Render. El entorno está protegido mediante el uso estricto de variables de entorno (`ENVIRONMENT VARIABLES`) para credenciales, tokens de Mercado Pago y configuraciones de base de datos.
* **Integración y Despliegue Continuo (CI/CD):** Ambos servicios (Vercel y Render) están enlazados directamente a la rama `main` de este repositorio de GitHub. Cualquier `push` o integración de código activa un *pipeline* automatizado que compila y despliega la nueva versión sin tiempo de inactividad.

---

## 🚀 Instalación y Uso Local

Si querés clonar este repositorio y levantar el entorno de desarrollo, seguí estos pasos:

1. **Cloná el repositorio:**
   ```bash
   git clone [https://github.com/tu-usuario/proclub-manager.git](https://github.com/tu-usuario/proclub-manager.git)

2. **Configurá tu base de datos en src/main/resources/application.properties:**
    ```bash
   # Base de Datos
    spring.datasource.url=jdbc:mysql://localhost:3306/tu_base_de_datos
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    
    # Configuración de Spring AI (Groq)
    spring.ai.openai.api-key=TU_API_KEY_AQUI
    spring.ai.openai.base-url=[https://api.groq.com/openai/v1](https://api.groq.com/openai/v1)
    spring.ai.openai.chat.options.model=openai/gpt-oss-120b
    
    # Configuración de Mercado Pago API
    mercadopago.access-token=TU_API_KEY_AQUI

3. **Ejecutá el Backend:**
   ```bash
   mvn spring-boot:run

(Nota: El proyecto incluye un DataLoader que "siembra" la base de datos con registros de prueba si se ejecuta con la propiedad ddl-auto=create. Es importante volver a poner update luego de usar el DataLoader).

4. **Ejecutá el Frontend:**
Abrí la carpeta /frontend y ejecutá el archivo index.html (se recomienda usar extensiones como Live Server en VS Code). Recordá modificar la constante API_BASE_URL en app.js para que apunte a http://localhost:8080 durante el desarrollo local


### Desarrollado por Julián Carmona - **[LinkedIn](https://www.linkedin.com/in/juli%C3%A1n-carmona-497b55286/)**

