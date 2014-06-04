/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.ws.mainServlets;

import com.google.gson.Gson;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
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
import org.unicauca.stcav.jmx.ManagementAttributeParser;
import org.unicauca.stcav.jmx.mbean.MBeanServerController;
import org.unicauca.stcav.jmx.model.TimeOfLife;
import org.university.stcav.model.Descriptor;
import org.university.stcav.model.Element;
import org.university.stcav.model.Layout;
import org.university.stcav.model.ProcessItem;
import org.university.stcav.persistence.BDMainController;
import org.university.stcav.persistence.entity.Evento;
import org.university.stcav.processor.FileProcessor;
import org.university.stcav.processor.InterpreterInteractiveService;

/**
 *
 * @author Administrador
 */
@WebServlet(name = "EventProcessorServlet", urlPatterns = {"/EventProcessorServlet"})
public class EventProcessorServlet extends HttpServlet {

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
        session.setMaxInactiveInterval(-1);
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
            case 0:// Eventos del programa
                jsonResponse = getEventsByIdPrg((Long) session.getAttribute("idPrg"));
                break;
            case 1:// Modificando el evento
                jsonResponse = modifyElementEventById(Long.parseLong(request.getParameter("id")), request.getParameter("nombre"), request.getParameter("descripcion"), session);
                break;
            case 2:// Eliminando el evento
                jsonResponse = deleteEventById(Long.parseLong(request.getParameter("id")));
                break;
            case 3:// Creando la referencia temporal del evento
                jsonResponse = createElementEventByIdPrg((Long) session.getAttribute("idPrg"), request.getParameter("nombre"), request.getParameter("descripcion"), session);
                break;
            case 4:// Guardando el evento
                jsonResponse = createEventByIdPrg(request.getParameter("descriptor"), session);
                break;
            case 5:// Cambiando el estado del evento el evento
                jsonResponse = modifyStateEventById(Long.parseLong(request.getParameter("id")), request.getParameter("estado"));
                break;
            case 6:// Cambiando el duracion del evento el evento
                jsonResponse = modifyDurationEventById(Long.parseLong(request.getParameter("id")), Double.parseDouble(request.getParameter("duracion")));
                break;
            case 7:// Obteniendo evento por id
                jsonResponse = getEventById(Long.parseLong(request.getParameter("id")));
                break;
            case 8:// Reeferencia de edicion de evento
                jsonResponse = ReferenceEventById(Long.parseLong(request.getParameter("id")), session);
                break;
            case 9:
                jsonResponse = get_descriptor_edit_event(session);
                break;
            case 10:
                jsonResponse = update_event(session, request.getParameter("descriptor"));
                break;
            case 11:
                jsonResponse = create_event_from_actual(request.getParameter("nombre"),request.getParameter("descripcion"),request.getParameter("descriptor"),session);
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
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotFoundException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotFoundException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(EventProcessorServlet.class.getName()).log(Level.SEVERE, null, ex);
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

    private String getEventsByIdPrg(Long aLong) {
        List<Evento> temp_cont = BDMainController.getEventsByProgram(aLong);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp_cont);
        System.out.println(res_json);
        return res_json;
    }

    private String deleteEventById(long parseLong) {
        if (BDMainController.deleteEventById(parseLong)) {
            return "El evento fue borrado satisfactoriamente";
        }
        return "La operacion no pudo ser realizada";
    }

    private String createElementEventByIdPrg(Long idPrg, String nombre, String descripcion, HttpSession session) {
        Evento e = new Evento();
        e.setProgramaidPrograma(idPrg);
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        session.setAttribute("eventTemp", e);
        return "nD4m.html";
    }

    private String createEventByIdPrg(String descriptor, HttpSession session) {
        Long idEventTemp = BDMainController.createEvent((Evento) session.getAttribute("eventTemp"), descriptor);
        System.out.println(descriptor);

        //Asociando las app interactivas
        Descriptor d;
        d = new Gson().fromJson(descriptor, Descriptor.class);
        InterpreterInteractiveService.processorLineInteractive(d.getLi().getElements(), ((Evento) session.getAttribute("eventTemp")).getIdEvento());

        //Editando el evento en multimedia
        ProcessItem pi = new ProcessItem(ProcessItem.EVENTO, ProcessItem.DESCRIPTORREADER, idEventTemp, "NaN", 1);
        Gson gson = new Gson();
        String postData = gson.toJson(pi);
        System.out.println(postData);
        FileProcessor.do_file_write(Layout.PATHSTACKVIDEOPROCESSOR + Layout.STACKVIDEOPROCESSOR, postData);
        return "redirect:/nD3.html";
    }

    private String modifyElementEventById(long id, String nombre, String desc, HttpSession session) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String modifyStateEventById(long id, String estado) {
        BDMainController.modifyStateEvent(id, estado);
        return "OK";
    }

    private String modifyDurationEventById(long id, double duration) {
        BDMainController.modifyDurationEventById(id, duration);
        return "OK";
    }

    private String getEventById(long id) {
        Evento temp = BDMainController.getEventById(id);
        JSONSerializer js = new JSONSerializer();
        String res_json = js.serialize(temp);
        System.out.println(res_json);
        return res_json;
    }

    private String ReferenceEventById(long idEve, HttpSession session) {
        session.setAttribute("EditEventID", idEve);
        System.out.println("Referencia temporal a evento: " + idEve);
        return "200 OK";
    }

    private String get_descriptor_edit_event(HttpSession session) {
        return BDMainController.getEventById((Long) session.getAttribute("EditEventID")).getDescriptor();
    }

    private String update_event(HttpSession session, String descriptor) {

        Long idEvent = (Long) session.getAttribute("EditEventID");
        //modificando el descriptor del evento
        BDMainController.modifyDescriptorEvent(idEvent,descriptor);
        System.out.println(descriptor);

        //Eliminando las app interactivas anteriores
        BDMainController.deleteAppsFromEvent(idEvent);

        //Asociando las app interactivas
        Descriptor d;
        d = new Gson().fromJson(descriptor, Descriptor.class);
        InterpreterInteractiveService.processorLineInteractive(d.getLi().getElements(), idEvent);

        //Editando el evento en multimedia
        ProcessItem pi = new ProcessItem(ProcessItem.EVENTO, ProcessItem.DESCRIPTORREADER, idEvent, "NaN", 1);
        Gson gson = new Gson();
        String postData = gson.toJson(pi);
        System.out.println(postData);
        FileProcessor.do_file_write(Layout.PATHSTACKVIDEOPROCESSOR + Layout.STACKVIDEOPROCESSOR, postData);
        return "redirect:/nD3.html";
    }

    private String create_event_from_actual(String name, String description, String descriptor, HttpSession session) {
        //System.out.println("Nombre: "+name+"desc: "+description+"descrip: "+descriptor);
        //Creando el evento
        Long lastIdEvent = (Long) session.getAttribute("EditEventID");
        Evento e = new Evento();
        e.setProgramaidPrograma(BDMainController.getEventById(lastIdEvent).getProgramaidPrograma());
        e.setNombre(name);
        e.setDescripcion(description);
        Long idEve = BDMainController.createEvent(e, descriptor);
        //Asociando las app interactivas
        Descriptor d;
        d = new Gson().fromJson(descriptor, Descriptor.class);
        InterpreterInteractiveService.processorLineInteractive(d.getLi().getElements(), idEve);

        //Editando el evento en multimedia
        ProcessItem pi = new ProcessItem(ProcessItem.EVENTO, ProcessItem.DESCRIPTORREADER, idEve, "NaN", 1);
        Gson gson = new Gson();
        String postData = gson.toJson(pi);
        System.out.println(postData);
        FileProcessor.do_file_write(Layout.PATHSTACKVIDEOPROCESSOR + Layout.STACKVIDEOPROCESSOR, postData);
        return "redirect:/nD3.html";
    }
}
