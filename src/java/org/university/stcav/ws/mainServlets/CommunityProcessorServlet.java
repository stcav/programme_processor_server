/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.ws.mainServlets;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.university.stcav.model.GridElement;
import org.university.stcav.persistence.BDMainController;
import org.university.stcav.persistence.controller.ComunidadJpaController;
import org.university.stcav.persistence.controller.ProgramaJpaController;
import org.university.stcav.persistence.entity.Comunidad;
import org.university.stcav.persistence.entity.Programa;

/**
 *
 * @author Administrador
 */
@WebServlet(name = "CommunityProcessorServlet", urlPatterns = {"/CommunityProcessorServlet"})
public class CommunityProcessorServlet extends HttpServlet {

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
            case 0:// Comunidades asociadas
                jsonResponse = getCommunities();
                break;
            case 1:// Contenidos de la comunidad
                //jsonResponse = getProgramsByIdCom(Long.parseLong(request.getParameter("idCom")));//OJO importante para la parrilla
                jsonResponse = getProgramsByIdCom((Long) session.getAttribute("idCommunity"));
                break;
            case 2:// Establecer horario de la grid de parrilla
                jsonResponse = setHourProgramById(Long.parseLong(request.getParameter("idPgr")), request.getParameter("grid_coor"));
                break;
            case 3://  retornar las coordenadas de los programas de la grid de parilla
                jsonResponse = getAllProgramsProgramed();
                break;
            case 4://  retornar las coordenadas de los programas de la grid de parilla
                jsonResponse = deleteProgramProgramed(request.getParameter("grid_coor"));
                break;
            case 5://  retornar las coordenadas de los programas de la grid de parilla
                jsonResponse = get_idCommunnity(session);
                break;
            case 6:
                jsonResponse = getProgramsByIdCom(Long.parseLong(request.getParameter("idCom")));//OJO importante para la parrilla
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

    private String getCommunities() {
        List<Comunidad> temp_cont = BDMainController.getComunities();
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp_cont);
        System.out.println(res_json);
        return res_json;
    }

    private String getProgramsByIdCom(long parseLong) {
        List<Programa> temp_cont = BDMainController.getProgramsByCommunity_np(parseLong);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp_cont);
        System.out.println(res_json);
        return res_json;
    }

    private String setHourProgramById(long idPgr, String coor) {
        System.out.println("Fijando nuevo horario ******************************************** para " + idPgr);
        Programa p = BDMainController.setHourProgram(idPgr, coor);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(p);
        System.out.println(res_json);
        return res_json;
    }

    private String getAllProgramsProgramed() {
        List<GridElement> temp_cont = BDMainController.getAllProgramsProgramed();
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp_cont);
        System.out.println(res_json);
        return res_json;
    }

    private String deleteProgramProgramed(String parameter) {
        GridElement ge = BDMainController.deleteProgramProgramed(parameter);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(ge);
        System.out.println(res_json);
        return res_json;
    }

    private String get_idCommunnity(HttpSession session) {
        return session.getAttribute("idCommunity").toString();
    }
}
