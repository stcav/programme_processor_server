/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.ws.mainServlets;

import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.university.stcav.persistence.BDMainController;
import org.university.stcav.persistence.entity.Programa;

/**
 *
 * @author Administrador
 */
@WebServlet(name = "ProgrammmeProcessorServlet", urlPatterns = {"/ProgrammmeProcessorServlet"})
public class ProgrammmeProcessorServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=iso-8859-1");

        String jsonResponse = null;
        switch (Integer.parseInt(request.getParameter("operation"))) {
            case 0:// Programas de la comunidad
                jsonResponse = getProgramsByIdCom((Long)session.getAttribute("idCommunity"));
                break;
            case 1:// Programa desde ID
                jsonResponse = getProgramById(Long.parseLong(request.getParameter("idPrg")));
                break;
            case 2:// Eliminando programa
                jsonResponse = deleteProgramById(Long.parseLong(request.getParameter("idPrg")));
                break;
            case 3:// Creando programa
                jsonResponse = createProgramByIdCom((Long) session.getAttribute("idCommunity"),request.getParameter("nombre"),request.getParameter("descripcion"));
                break;
            case 4:// Editando programa
                jsonResponse = modifyProgramById(request.getParameter("id"),request.getParameter("nombre"),request.getParameter("descripcion"));
                break;
            case 5:// obteniendo el numero de eventos del programa
                jsonResponse = getProgramEventNumberById(Long.parseLong(request.getParameter("idPrg")));
                break;
            default:
                break;
        }
        PrintWriter out = response.getWriter();
        try {
            out.println(jsonResponse);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String getProgramsByIdCom(long id) {
        List<Programa> temp_cont = BDMainController.getProgramsByCommunity(id);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp_cont);
        System.out.println(res_json);
        return res_json;
    }

    private String getProgramById(long parseLong) {
        Programa p = BDMainController.getProgramById(parseLong);
        JSONSerializer js = new JSONSerializer();
        if (p != null) {
            return js.serialize(p);
        } else {
            return null;
        }
    }

    private String deleteProgramById(long parseLong) {
        if (BDMainController.deleteProgramById(parseLong)) {
            return "El programa fue borrado satisfactoriamente";
        }
        return "La operacion no pudo ser realizada";
    }

    private String createProgramByIdCom(Long idCom, String nombre, String descripcion) {
        BDMainController.createProgram(idCom, nombre, descripcion);
        return "redirect:/D1.html";
    }

    private String modifyProgramById(String id, String nombre, String desc) {
        BDMainController.editProgram(Long.parseLong(id), nombre, desc);
        return "OK";
    }

    private String getProgramEventNumberById(long id) {
        return BDMainController.getProgramEventNumberById(id);
    }

 
}
