package com.portfolio.club_manager.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatClient chatClient;

    public ChatbotController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/preguntar")
    public Map<String, String> responderConsulta(@RequestBody Map<String, String> request) {
        String mensajeUsuario = request.get("mensaje");

        String contexto = "Eres el asistente virtual de ClubSync. Tu objetivo es informar a los administradores de forma amable, natural y fluida. " +
                "REGLA 1: Usa las herramientas para buscar información en el sistema. LUEGO, lee esos datos y REDACTA una respuesta conversacional. NO hagas 'copiar y pegar'. " +
                "REGLA 2: NUNCA menciones que usaste una 'herramienta', ni hables de 'JSON'. " +
                "REGLA 3: ESTÁ ESTRICTAMENTE PROHIBIDO usar formato de código, comillas literales, llaves '{}' o imprimir caracteres de escape como '\\n'. " +
                "Actúa como un humano resolutivo, saluda brevemente y entrega la información clara y ordenada.";
        try {
            String respuestaIA = chatClient.prompt()
                    .user(contexto + "\n\nPregunta: " + mensajeUsuario)
                    
                    .functions("consultarPersonaTool", "contarEstadisticasTool", "consultarInstalacionesTool", "listarMorososTool", "consultarActividadesTool") 
                    .call()
                    .content();
                    
            return Map.of("respuesta", respuestaIA);

        } catch (Exception e) {
            System.out.println("Error de IA: " + e.getMessage());
            e.printStackTrace(); 
            return Map.of("respuesta", "Mis servidores están en mantenimiento. 🛠️");
        }
    }
}