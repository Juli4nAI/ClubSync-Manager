package com.portfolio.club_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portfolio.club_manager.entities.Pago;
import com.portfolio.club_manager.services.PagoService;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> descargarComprobantePDF(@PathVariable Integer id) {
        
        Pago pago = pagoService.buscarPorId(id); 
        
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font textoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font resaltadoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            
            Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO - CLUB MANAGER", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            document.add(new Paragraph("Fecha: " + pago.getFechaHora(), textoFont)); 
            document.add(new Paragraph("Socio: " + pago.getPersona().getApynom(), textoFont)); 
            
            
            String metodoReal = pago.getMetodo() != null ? pago.getMetodo() : "Mercado Pago";
            document.add(new Paragraph("Método: " + metodoReal, textoFont));
            
            Paragraph montoPar = new Paragraph("Monto Abonado: $" + pago.getMonto(), resaltadoFont);
            montoPar.setSpacingBefore(10);
            document.add(montoPar);

            
            Paragraph estadoPago;
            if (pago.getPersona().getDeuda() != null && pago.getPersona().getDeuda() > 0) {
                estadoPago = new Paragraph("ESTADO: Pago Parcial (Saldo restante: $" + pago.getPersona().getDeuda() + ")", textoFont);
            } else {
                estadoPago = new Paragraph("ESTADO: Deuda Totalmente Saldada", resaltadoFont);
            }
            estadoPago.setSpacingBefore(10);
            document.add(estadoPago);

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Comprobante_" + pago.getId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().build();
        }
    }
}