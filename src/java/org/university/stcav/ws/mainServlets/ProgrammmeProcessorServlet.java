/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.ws.mainServlets;

import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.university.stcav.pps.jmx.ManagementAttributeParser;
import org.unicauca.stcav.jmx.mbean.MBeanServerController;
import org.unicauca.stcav.jmx.model.TimeOfLife;
import org.university.stcav.model.Layout;
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
            throws ServletException, IOException, MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, MalformedObjectNameException {
        //adding support to quantify time of response (for managment skill support)
        TimeOfLife tol = new TimeOfLife();
        tol.set_home_time();
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=iso-8859-1");

        String jsonResponse = null;
        int conteo=0;
        String managementRecord="";
        int macroattributeID = 0;
        int attributeID = 0;
        
        if (request.getParameter("management_record")!=null){
            managementRecord= request.getParameter("management_record");
            attributeID = Integer.parseInt(managementRecord);
            macroattributeID = Integer.parseInt(String.valueOf(managementRecord.charAt(0)));
        }
        
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
            //set to response endtime  
            tol.set_end_time();
            if(!managementRecord.equals("") && request.getParameter("ignore_metric") == null){
                System.out.println("--> saving metric");
                ManagementAttributeParser map = ManagementAttributeParser.getInstance();
                // Setting the counts and time attributes changed of associated MBeanAttributeInfo
                // Attribute Counts
                conteo = (Integer) MBeanServerController.getAttribute(Layout.JMXDOMAIN, Layout.PROGRAMMEPROCESSORSERVER, map.getManagementAttributeName(macroattributeID), map.getManagementAttributeName(attributeID)+"Counts");
                System.out.println("--> "+conteo);
                MBeanServerController.changeAttribute(Layout.JMXDOMAIN, Layout.PROGRAMMEPROCESSORSERVER, map.getManagementAttributeName(macroattributeID), map.getManagementAttributeName(attributeID)+"Counts", String.valueOf(conteo+1));
                // Attribute Time
                MBeanServerController.changeAttribute(Layout.JMXDOMAIN, Layout.PROGRAMMEPROCESSORSERVER, map.getManagementAttributeName(macroattributeID), map.getManagementAttributeName(attributeID)+"Time", String.valueOf(tol.get_tot_()));    
            }
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
        try {
            processRequest(request, response);
        } catch (MBeanException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotFoundException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (MBeanException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotFoundException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(ProgrammmeProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
