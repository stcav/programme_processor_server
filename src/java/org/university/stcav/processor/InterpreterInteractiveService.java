/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.processor;
import java.util.Iterator;
import java.util.List;
import org.university.stcav.model.Element;
import org.university.stcav.persistence.BDMainController;
import org.university.stcav.persistence.entity.InfoAsociada;
import org.university.stcav.persistence.entity.Valoracion;

/**
 *
 * @author JoGa
 */
public class InterpreterInteractiveService {

//***************************************** PRINCIPAL *************************************************//
    public static void processorLineInteractive(List<Element> elements, Long idEvent) {
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            Element temp = (Element) iter.next();
            //Analizando si el servicio es INFOASOCIADA
            if (Long.parseLong(temp.getId()) == 1L) {
                InfoAsociada ia = new InfoAsociada();
                ia.setDuracion(Integer.parseInt(temp.getEnd())-Integer.parseInt(temp.getHome()));
                ia.setEventoidEvento(idEvent);
                ia.setTexto(temp.getText());
                ia.setTiempodespliegue(Integer.parseInt(temp.getHome()));
                BDMainController.createInstanceAssociatedService(ia);
            } //Analizando si el servicio es VALORACION
            else if (Long.parseLong(temp.getId()) == 2L) {
                Valoracion v = new Valoracion();
                v.setEventoidEvento(idEvent);
                v.setTexto(temp.getText());
                BDMainController.createInstanceAssesment(v);
            }
        }

    }
}
