/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.persistence;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.university.stcav.model.GridElement;
import org.university.stcav.persistence.controller.ComunidadJpaController;
import org.university.stcav.persistence.controller.EventoJpaController;
import org.university.stcav.persistence.controller.InfoAsociadaJpaController;
import org.university.stcav.persistence.controller.ProgramaJpaController;
import org.university.stcav.persistence.controller.ValoracionJpaController;
import org.university.stcav.persistence.controller.exceptions.NonexistentEntityException;
import org.university.stcav.persistence.entity.Comunidad;
import org.university.stcav.persistence.entity.Evento;
import org.university.stcav.persistence.entity.InfoAsociada;
import org.university.stcav.persistence.entity.Programa;
import org.university.stcav.persistence.entity.Valoracion;

/**
 *
 * @author Administrador
 */
public class BDMainController {

//***************************** INICIALIZACION *********************************
    public static boolean communityAssociator(long id) {
        Comunidad c = new ComunidadJpaController().findComunidad(id);
        if (c != null) {
            return true;
        } else {
            return false;
        }
    }

//******************************* GESTION DE SERVICIOS *****************************//
    public static void createInstanceAssociatedService(InfoAsociada ia) {
        InfoAsociadaJpaController iajc = new InfoAsociadaJpaController();
        iajc.create(ia);
    }

    public static void createInstanceAssesment(Valoracion v) {
        ValoracionJpaController vjc = new ValoracionJpaController();
        vjc.create(v);
    }

//******************************* PROGRAMAS ************************************
    public static Vector getProgramsByCommunity(long idCom) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Vector programs = new Vector();
        Programa p = new Programa();
        System.out.println(pjc.getProgramaCount());
        List<Programa> temp_prg = pjc.findProgramaEntities();
        Iterator iter = temp_prg.iterator();
        while (iter.hasNext()) {
            p = (Programa) iter.next();
            if (p.getComunidadidComunidad() == idCom) {
                //System.out.println("+++++++++++++++++ "+c.getFechacreacion());
                programs.add(p);
            }
        }
        temp_prg = null;
        System.gc();
        return programs;
    }

    public static Vector getProgramsByCommunity_np(long idCom) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Vector programs = new Vector();
        Programa p = new Programa();
        List<Programa> temp_prg = pjc.findProgramaEntities();
        Iterator iter = temp_prg.iterator();
        while (iter.hasNext()) {
            p = (Programa) iter.next();
            if (p.getComunidadidComunidad() == idCom && p.getDia().equals("0")) {
                //System.out.println("+++++++++++++++++ "+c.getFechacreacion());
                programs.add(p);
            }
        }
        temp_prg = null;
        System.gc();
        return programs;
    }

    public static Programa getProgramById(long idPrg) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        return pjc.findPrograma(idPrg);
    }

    public static boolean deleteProgramById(long idPrg) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        try {
            pjc.destroy(idPrg);
            //Falta eliminar referencias enlazadas a los eventos****************
            return true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public static void createProgram(long idCom, String nombre, String Descripcion) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Programa p = new Programa();
        p.setComunidadidComunidad(idCom);
        p.setNombre(nombre);
        p.setDescripcion(Descripcion);
        p.setRutascreenshot("default.jpg");
        p.setDia("0");
        pjc.create(p);
    }
    //******************************** EVENTOS ************************************

    public static Vector getEventsByProgram(long idPrg) {
        EventoJpaController ejc = new EventoJpaController();
        Vector events = new Vector();
        Evento e = new Evento();
        List<Evento> temp_prg = ejc.findEventoEntities();
        Iterator iter = temp_prg.iterator();
        while (iter.hasNext()) {
            e = (Evento) iter.next();
            if (e.getProgramaidPrograma() == idPrg) {
                //System.out.println("+++++++++++++++++ "+c.getFechacreacion());
                events.add(e);
            }
        }
        temp_prg = null;
        System.gc();
        return events;
    }

    public static Evento getEventById(long id) {
        EventoJpaController ejc = new EventoJpaController();
        return ejc.findEvento(id);
    }

    public static boolean deleteEventById(long idEve) {
        EventoJpaController ejc = new EventoJpaController();
        try {
            ejc.destroy(idEve);
            //Falta eliminar referencias enlazadas a los eventos****************
            return true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public static Long createEvent(Evento e, String descriptor) {
        EventoJpaController ejc = new EventoJpaController();
        e.setDescriptor(descriptor);
        //e.setDuracion();
        Long cm = System.currentTimeMillis();
        e.setRuta(e.getProgramaidPrograma() + "_" + cm + ".mp4");
        e.setFechacreacion(new Date(cm));
        e.setEstado("sin_procesar");
        ejc.create(e);
        return findEventbySrc(e.getProgramaidPrograma() + "_" + cm + ".mp4");

    }

    public static Long findEventbySrc(String src) {
        EventoJpaController cjc = new EventoJpaController();
        List<Evento> temp_list = cjc.findEventoEntities();
        Evento c = new Evento();
        // Obtenemos un Iterador y recorremos la lista.
        Iterator iter = temp_list.iterator();
        while (iter.hasNext()) {
            c = (Evento) iter.next();
            System.out.println(c.getRuta() + " " + src);
            if (c.getRuta().equals(src)) {
                cjc = null;
                temp_list = null;
                return c.getIdEvento();
            }
        }

        return null;
    }

    public static void modifyStateEvent(Long id, String state) {
        EventoJpaController ejc = new EventoJpaController();
        Evento e = new Evento();
        e = ejc.findEvento(id);
        e.setEstado(state);
        try {
            ejc.edit(e);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void modifyDurationEventById(Long id, double dur) {
        EventoJpaController ejc = new EventoJpaController();
        Evento e = new Evento();
        e = ejc.findEvento(id);
        String dur_ = do_SecondsToTime(dur);
        e.setDuracion(new BigDecimal(dur_));
        try {
            ejc.edit(e);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void modifyDescriptorEvent(Long idEvent, String descriptor) {
        EventoJpaController ejc = new EventoJpaController();
        Evento e = new Evento();
        e = ejc.findEvento(idEvent);
        e.setDescriptor(descriptor);
        try {
            ejc.edit(e);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteAppsFromEvent(Long idEvent) {
        //Eliminado las app de info_asociada
        InfoAsociadaJpaController iajc = new InfoAsociadaJpaController();
        List <InfoAsociada> ias = iajc.findInfoAsociadaEntities();
        Iterator iter = ias.iterator();
        InfoAsociada ia = new InfoAsociada();
        while(iter.hasNext()){
            ia = (InfoAsociada)iter.next();
            if(ia.getEventoidEvento()==idEvent){
                try {
                    iajc.destroy(ia.getIdInfoAsociada());
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //Eliminando las app de valoracion
        ValoracionJpaController vjc = new ValoracionJpaController();
        List <Valoracion> vs = vjc.findValoracionEntities();
        iter = vs.iterator();
        Valoracion v = new Valoracion();
        while(iter.hasNext()){
            v = (Valoracion) iter.next();
            if(v.getEventoidEvento()==idEvent){
                try {
                    vjc.destroy(v.getIdValoracion());
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

//******************************** PARRILLA ********************************
    public static Vector getAllProgramsProgramed() {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Programa p = new Programa();
        Vector gridElements = new Vector();

        String ubication;
        List<Programa> temp_prg = pjc.findProgramaEntities();
        Iterator iter = temp_prg.iterator();
        while (iter.hasNext()) {
            p = (Programa) iter.next();
            if (!p.getDia().equals("0")) {
                GridElement ge = new GridElement();
                //estableciendo las coordenadas para la ubicacion del programa en la parrilla, ver algortimo para espacios de 30 min
                ubication = ((p.getHora().getHours() * 2) + (p.getHora().getMinutes() / 30)) + "_" + p.getDia() + "_";
                System.out.println(ubication);
                ge.setUbication(ubication);
                ge.setName(p.getNombre());
                gridElements.add(ge);
            }
        }
        System.gc();
        return gridElements;
    }

    public static String getProgramEventNumberById(Long idPrg) {
        return getEventsByProgram(idPrg).size() + "";
    }

    public static List getComunities() {
        ComunidadJpaController cjc = new ComunidadJpaController();
        return cjc.findComunidadEntities();
    }

    public static Programa setHourProgram(long idPgr, String coor) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Programa p = pjc.findPrograma(idPgr);
        int hora;
        int minutos;
        int i = Integer.parseInt(coor.split("_")[1]);
        if ((i % 2) == 0) {
            hora = (i / 2);
            minutos = 0;
        } else {
            hora = ((i - 1) / 2);
            minutos = 30;
        }
        Time t = new Time(hora, minutos, 0);

        p.setDia(coor.split("_")[2]);
        p.setHora(t);
        try {
            pjc.edit(p);
            return p;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static GridElement deleteProgramProgramed(String grid_coor) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Programa p = new Programa();
        GridElement ge = new GridElement();
        String new_grid_coor = grid_coor.substring(2);
        String ubication;

        List<Programa> temp_prg = pjc.findProgramaEntities();
        Iterator iter = temp_prg.iterator();
        while (iter.hasNext()) {
            p = (Programa) iter.next();
            if (!p.getDia().equals("0")) {
                //estableciendo las coordenadas para la ubicacion del programa en la parrilla, ver algortimo para espacios de 30 min
                ubication = ((p.getHora().getHours() * 2) + (p.getHora().getMinutes() / 30)) + "_" + p.getDia() + "_";
                System.out.println(ubication);
                if (new_grid_coor.equals(ubication)) {
                    ge.setUbication(grid_coor);
                    ge.setName("");
                    p.setDia("0");
                    p.setHora(new Time(0, 0, 0));
                    try {
                        pjc.edit(p);
                    } catch (NonexistentEntityException ex) {
                        Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return ge;
                }

            }
        }
        System.gc();
        ge.setUbication("0");
        return ge;//no se hayo coincidencia
    }

    public static void editProgram(Long id, String nombre, String desc) {
        ProgramaJpaController pjc = new ProgramaJpaController();
        Programa p = pjc.findPrograma(id);
        p.setNombre(nombre);
        p.setDescripcion(desc);
        try {
            pjc.edit(p);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BDMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //******************* OTHERS ****************************//
    public static String do_SecondsToTime(double seconds) {
        String hours_ = "";
        String min_ = "";
        String sec_ = "";
        int hours = (int) (Math.round(seconds) / 3600);
        if (hours < 10) {
            hours_ = "0" + hours;
        } else {
            hours_ = "" + hours;
        }
        int min = (int) ((Math.round(seconds) - (hours * 3600)) / 60);
        if (min < 10) {
            min_ = "0" + min;
        } else {
            min_ = "" + min;
        }
        double sec = seconds - (hours * 3600) - (min * 60);
        if (sec < 10) {
            sec_ = "0" + sec;
        } else {
            sec_ = "" + sec;
        }

        return "20120101" + hours_ + min_ + sec_;
    }


}
