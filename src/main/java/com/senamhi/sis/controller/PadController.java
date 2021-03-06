package com.senamhi.sis.controller;

import com.senamhi.sis.connection.ConeccionDB;
import com.senamhi.sis.functions.Util;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Kori
 */
@Controller
@RequestMapping("/")
public class PadController {

//INICIO LISTA DE EXPEDIENTE DEL PAD BASE        
    @RequestMapping(value = {"/pad/mant_expedientes_pad"}, method = RequestMethod.GET)
	public String MantExpedientesPad(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","EXPEDIENTES DEL PAD");
            
            ConeccionDB cn =  new ConeccionDB();   
            Util util = new Util();
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, "");
            request.setAttribute("abogado", cb_abogado);
                        
            return "pad/mant_expedientes_pad";
	}
//FIN LISTA DE EXPEDIENTE DEL PAD BASE
//
//INICIO LISTA DE EXPEDIENTE DEL PAD TABLA        
    @RequestMapping(value = {"/pad/mant_expedientes_pad_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryExpedientePadTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String abogado = request.getParameter("abogado");
            
            ConeccionDB cn =  new ConeccionDB();
            String np = "";
            
            if (abogado.equals("undefined")){
                abogado = "";
            }
            if (!abogado.equals("")){
                np = "pad.fn_expediente_pad_abogado_consulta";
            }else{
                np = "pad.fn_expediente_pad_consulta";
            }
            
            String array[] = new String[1];
            array[0] = abogado;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);
            
            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String v_id_exp = vss.get(0).toString();
                String d_fec_exp = vss.get(1).toString();
                String v_descripcion_etapa = vss.get(2).toString();                
                String v_id_denunciante = vss.get(3).toString();                
                String v_abogado = vss.get(5).toString();                
                String d_fec_presc_ipad = vss.get(7).toString();                
                String investigado = vss.get(15).toString();                
                
                String chkb = "<div class='form-group dropdown text-center'>"
                            + "<input type='checkbox' value='"+v_id_exp+'_'+d_fec_exp+'_'+v_descripcion_etapa+"' id='cb_exp_"+v_id_exp+"' class='cb_exp' onchange='pad_lista_exp_asigna_abogado_tmp(this)' />"
                            + "<label for='cb_exp_"+v_id_exp+"'></label>"
                            + "</div>";
                
                String boton = "<div class='form-group dropdown'>"
                            + "<button type='button' class='btn btn-info dropdown-toggle' data-toggle='dropdown' id='herramientas'>"
                            + "<span class='glyphicon glyphicon-wrench'>"
                            + "</span>"
                            + "</button>"
                            + "<ul class='dropdown-menu dropdown-menu-left'>" 
                            + "<li class='divider'></li>"
                            + "<li><a onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+v_id_exp+"\\\")'>Consultar expediente</a></li>"
                            + "<li><a onclick='pad_mant_expedientes_pad_modifica_popup(\\\""+v_id_exp+"\\\")'>Adicionar documento</a></li>"
                            + "<li class='divider'></li>"
                            + "<li  class=''><a onclick='pad_mant_investigado_consulta_popup(\\\""+v_id_exp+"\\\")'>Consultar investigados</a></li>"
                            + "<li  class=''><a onclick='pad_mant_investigado_popup(\\\""+v_id_exp+"\\\")'>Adicionar investigado</a></li>"
                            + "<li class='divider'></li>"  
                            + "</ul>"
                            + "</div>";                          
                
                Vector vv = new Vector();
                vv.add(chkb);
                vv.add(v_id_exp);
                vv.add(d_fec_exp);
                vv.add(d_fec_presc_ipad);
                vv.add(v_descripcion_etapa);
                vv.add(v_id_denunciante);
                vv.add(investigado);
                vv.add(v_abogado);
                vv.add(boton);
                v_temp.add(vv);                
            }    
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'80%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'_'} , "
                                    + "{'sTitle':'EXPEDIENTE'} , "
                                    + "{'sTitle':'FECHA RECEP.ORH'} , "
                                    + "{'sTitle':'FECHA PRESCR.INICIO PAD'} , "
                                    + "{'sTitle':'ETAPA'} , "
                                    + "{'sTitle':'DENUNCIANTE'} , "
                                    + "{'sTitle':'DENUNCIADO(S)'} , "
                                    + "{'sTitle':'ABOGADO'} , "
                                    + "{'sTitle':'HERRAMIENTAS'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumnDefs");sv.add("[{ sClass:'dt-center','aTargets':[0,1,2,3,4,5,6,7,8]},{'aTargets':[ 3,4 ],'bVisible': true,'bSearchable': true}]");vc_tbl.add(sv);sv =  new Vector();
            //boton de excel
            sv.add("dom");sv.add("'Bfrtip'");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6,7]}  },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6,7]},orientation:'landscape',pageSize:'A4' },"
                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6,7]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='2' class='table table-striped table-hover table-bordered table-responsive-sm' id='c_tbl_exp_pad'></table>";
            String tbl = util.datatable("c_tbl_exp_pad",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_expedientes_pad_tbl";
	}
//FIN LISTA DE EXPEDIENTE DEL PAD TABLA            
//
//INICIO MANTENIMIENTO NUEVO EXPEDIENTE DEL PAD POPUP            
    @RequestMapping(value = {"/pad/mant_expedientes_pad_popup"}, method = RequestMethod.GET)
    public String MantExpedientePadPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","REGISTRO DE EXPEDIENTE");     

        try {
            String id = request.getParameter("id");
            
            Calendar c = Calendar.getInstance();//Anio actual para el registro del expediente           
            String anio = Integer.toString(c.get(Calendar.YEAR));
            
            Date date = new Date();//Fecha de registro del documento (por referencia)
            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
            String fecharecep = formatofec.format(date);        
                        
            //Fecha del documeto, por defecto el d�a actual            
            DateFormat fecDoc = new SimpleDateFormat("dd/MM/yyyy");
            String fec_doc = fecDoc.format(date); 
            
            //Fecha de prescripci�n del inicio del PAD
//            Date nuevaFecha = new Date();
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DAY_OF_YEAR, 365);
//            nuevaFecha = cal.getTime();
//            String fecpresc_iniPAD = formatofec.format(nuevaFecha); 
            
            ConeccionDB cn = new ConeccionDB(); 
            Util util =  new Util();

//          informaci�n para el combo periodo (a�o)
            String ne = "pad.fn_anio_consulta";
            String array_cbo_anio[] = new String[1];
            array_cbo_anio[0] = "";
            Vector datos_cbo_anio = cn.EjecutarProcedurePostgres(ne, array_cbo_anio);            
            String cb_anio = util.contenido_combo(datos_cbo_anio, anio);
            request.setAttribute("anio", cb_anio);  
            
            request.setAttribute("fecharecep", fecharecep);
            request.setAttribute("fecdoc", fec_doc);
            
//          informaci�n para el combo Etapa
            String etapa = "pad.fn_etapa_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo_etapa = cn.EjecutarProcedurePostgres(etapa, array_cbo);
            String cb_etapa = util.contenido_combo(datos_cbo_etapa, "");
            request.setAttribute("etapa", cb_etapa);
            
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, "");
            request.setAttribute("abogado", cb_abogado);
            
//          informaci�n para el combo Dependencia
            String dependencia = "pad.fn_dependencia_consulta";
            String array_cbo_dependencia[] = new String[1];
            array_cbo_dependencia[0] = "";
            Vector datos_cbo_dependencia = cn.EjecutarProcedurePostgres(dependencia, array_cbo_dependencia);
            String cb_dependencia = util.contenido_combo(datos_cbo_dependencia, "");
            request.setAttribute("dependencia", cb_dependencia);
            
//            informaci�n combos: denunciante, remite y destino
            String cons_remite_uo = "senamhi.fn_destino_consulta";//consulta combo destino                  
            String array_remite_uo[] = new String[1];
            array_remite_uo[0] = "";
            Vector datos_cbo_uo_remite = cn.EjecutarProcedurePostgres(cons_remite_uo, array_remite_uo); 
            String cb_uo_rmte = util.contenido_combo(datos_cbo_uo_remite, "");
            request.setAttribute("remite", cb_uo_rmte); 
            
//            informaci�n combos: denunciante, remite y destino
            String cons_destino_uo = "senamhi.fn_destino_consulta";//consulta combo destino                  
            String array_destino_uo[] = new String[1];
            array_destino_uo[0] = "";
            Vector datos_cbo_uo_destino = cn.EjecutarProcedurePostgres(cons_destino_uo, array_destino_uo); 
            String cb_uo_dest = util.contenido_combo(datos_cbo_uo_destino, "00122760");//UFS por defecto
            request.setAttribute("destino", cb_uo_dest); 
            
//            informaci�n tipo de documento
            String ntdoc = "sgd.fn_clasifdoc_seriedoc_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_tdoc[] = new String[1];
            array_cbo_tdoc[0] = "90000048";
            Vector datos_cbo_tdoc = cn.EjecutarProcedurePostgres(ntdoc, array_cbo_tdoc);
            String cb_desc_clsfdoc = util.contenido_combo(datos_cbo_tdoc, "110");
            request.setAttribute("clsfdoc", cb_desc_clsfdoc);            
          
//            informaci�n investigado
            String investigado = "pad.fn_organo_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_invest[] = new String[1];
            array_cbo_invest[0] = "";
            Vector datos_cbo_invest = cn.EjecutarProcedurePostgres(investigado, array_cbo_invest);
            String cb_desc_invest = util.contenido_combo(datos_cbo_invest, "");
            request.setAttribute("investigado", cb_desc_invest);      
            
//          informaci�n para el combo Cargo
            String cargo = "pad.fn_cargo_consulta";
            String array_cbo_cargo[] = new String[1];
            array_cbo_cargo[0] = "";
            Vector datos_cbo_cargo = cn.EjecutarProcedurePostgres(cargo, array_cbo_cargo);
            String cb_cargo = util.contenido_combo(datos_cbo_cargo, "");
            request.setAttribute("cargo", cb_cargo);            
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_expedientes_pad_popup";
    }   
//FIN MANTENIMIENTO NUEVO EXPEDIENTE DEL PAD POPUP
//    
//INICIO MANTENIMIENTO EXPEDIENTE GUARDAR    
@RequestMapping(value = {"/pad/mant_expedientes_pad_guardar"}, method = RequestMethod.GET)
    public String MantExpedientePadGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String codUser = (String) session.getAttribute("codUser");//id del usuario

        String id = request.getParameter("id");
        String fecharecep = request.getParameter("fecharecep");
        String fecpresc_iniPAD = request.getParameter("fecpresc_iniPAD");
        String etapa = request.getParameter("etapa");
        String denunciante = request.getParameter("denunciante");
        String dependencia = request.getParameter("dependencia");
        String abogado = request.getParameter("abogado");
        String documento = request.getParameter("documento");
        String nrodoc = request.getParameter("nrodoc");        
        String fechadoc = request.getParameter("fechadoc");        
        String folio = request.getParameter("folio");        
        String plazo = request.getParameter("plazo");        
        String remite = request.getParameter("remite");        
        String destino = request.getParameter("destino");        
        String asunto =  request.getParameter("asunto").trim();
        asunto = asunto.replace("\"","'");
        asunto = asunto.replace("\n", "");
        String observacion =  request.getParameter("observacion").trim();      
        String iddoc = request.getParameter("iddoc");  
        String instructor = request.getParameter("instructor");  
        String uo_instructor = request.getParameter("uo_instructor");  
        String sancionador = request.getParameter("sancionador");  
        String uo_sancionador = request.getParameter("uo_sancionador");  
        String fecnotif_iniPAD = request.getParameter("fecnotif_iniPAD");  
        String fecpres_PAD = request.getParameter("fecpres_PAD");  
        String tipo_proced = request.getParameter("tipo_proced");  
        String estado = request.getParameter("estado");  
        String uo_remite = request.getParameter("uo_remite");  
        String tiempo = request.getParameter("tiempo"); 
//        
        String var_request = "";

        try {                    
            ConeccionDB cdb = new ConeccionDB(); 
            String np = "pad.fn_expediente_pad_mant";
            String array[] = new String[28];
            array[0] = id;
            array[1] = fecharecep;
            array[2] = fecpresc_iniPAD;
            array[3] = etapa;
            array[4] = denunciante;
            array[5] = dependencia;
            array[6] = abogado;
            array[7] = codUser;
            array[8] = documento;
            array[9] = nrodoc;
            array[10] = fechadoc;
            array[11] = folio;
            array[12] = plazo;
            array[13] = remite;
            array[14] = destino;
            array[15] = asunto;
            array[16] = observacion;
            array[17] = iddoc;
            array[18] = instructor;
            array[19] = sancionador;
            array[20] = fecnotif_iniPAD;
            array[21] = fecpres_PAD;
            array[22] = tipo_proced;
            array[23] = estado;
            array[24] = uo_instructor;
            array[25] = uo_sancionador;
            array[26] = uo_remite;
            array[27] = tiempo;
            
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);

            var_request = new Util().vector2json(datos);
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_expedientes_pad_guardar";
    }
//FIN MANTENIMIENTO EXPEDIENTE GUARDAR     
//
//    
//INICIO MANTENIMIENTO EXPEDIENTE NUEVO GUARDAR    
@RequestMapping(value = {"/pad/mant_expedientes_pad_nuevo_guardar"}, method = RequestMethod.GET)
    public String MantExpedientePadNuevoGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String codUser = (String) session.getAttribute("codUser");//id del usuario

        String id = request.getParameter("id");
        String fecharecep = request.getParameter("fecharecep");
        String fecpresc_iniPAD = request.getParameter("fecpresc_iniPAD");
        String etapa = request.getParameter("etapa");
        String denunciante = request.getParameter("denunciante");
        String dependencia = request.getParameter("dependencia");
        String abogado = request.getParameter("abogado");
        String documento = request.getParameter("documento");
        String nrodoc = request.getParameter("nrodoc");        
        String fechadoc = request.getParameter("fechadoc");        
        String folio = request.getParameter("folio");        
        String plazo = request.getParameter("plazo");        
        String remite = request.getParameter("remite");        
        String destino = request.getParameter("destino");        
        String asunto =  request.getParameter("asunto").trim();
        asunto = asunto.replace("\"","'");
        asunto = asunto.replace("\n", "");
        String observacion =  request.getParameter("observacion").trim();      
        String iddoc = request.getParameter("iddoc");  
        String instructor = request.getParameter("instructor");  
        String sancionador = request.getParameter("sancionador");  
        String fecnotif_iniPAD = request.getParameter("fecnotif_iniPAD");  
        String fecpres_PAD = request.getParameter("fecpres_PAD");  
        String anio = request.getParameter("anio");  
        String tiempo = request.getParameter("tiempo");  
        String investigado = request.getParameter("investigado");
        String cargo = request.getParameter("cargo");
        
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();
            String np = "pad.fn_expediente_pad_mant_nuevo";
            String array[] = new String[26];
            array[0] = id;
            array[1] = fecharecep;
            array[2] = fecpresc_iniPAD;
            array[3] = etapa;
            array[4] = denunciante;
            array[5] = dependencia;
            array[6] = abogado;
            array[7] = codUser;
            array[8] = documento;
            array[9] = nrodoc;
            array[10] = fechadoc;
            array[11] = folio;
            array[12] = plazo;
            array[13] = remite;
            array[14] = destino;
            array[15] = asunto;
            array[16] = observacion;
            array[17] = iddoc;
            array[18] = instructor;
            array[19] = sancionador;
            array[20] = fecnotif_iniPAD;
            array[21] = fecpres_PAD;
            array[22] = anio;
            array[23] = tiempo;
            array[24] = investigado;
            array[25] = cargo;
            
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);

            var_request = new Util().vector2json(datos);
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_expedientes_pad_nuevo_guardar";
    }
//FIN MANTENIMIENTO EXPEDIENTE NUEVO GUARDAR     
//
//INICIO SUBIR DOCUMENTOS
@RequestMapping(value = {"/pad/uploadfile"}, method = RequestMethod.POST)
    public String uploadfile(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
        
        ConeccionDB cdb = new ConeccionDB();
        
        String UPLOAD_DIRECTORY = "/home/glassfish/glassfish4/glassfish/domains/domain1/applications/files/pad"; 
        String msj = "";
                
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                
                if (isMultipart) {
                    // Create a factory for disk-based file items
                    FileItemFactory factory = new DiskFileItemFactory();
                    
                    // Create a new file upload handler
                    ServletFileUpload upload = new ServletFileUpload(factory);
                    
                    try {                       
                        List items = upload.parseRequest(request);
                        Iterator iterator = items.iterator();
                        while (iterator.hasNext()) {
                            FileItem item = (FileItem) iterator.next();
                            if (!item.isFormField()) {
                                String fileName = item.getName();  
                                String fileName_1 = item.getName();  
                                String field = item.getFieldName();
                                    StringTokenizer str =  new StringTokenizer(field,"|");
                                    String anio = str.nextToken();
                                    String exp = str.nextToken();
                                    String iddoc = str.nextToken();
                                    String in = str.nextToken();
                                    String dir = ""+anio+"_"+exp+"_"+iddoc+"";
                                    dir = DigestUtils.md5Hex(dir);                               
                                
                                fileName = DigestUtils.md5Hex(fileName);
                                File path = new File(UPLOAD_DIRECTORY+"/"+anio+"/"+"/"+dir);
                                if (!path.exists()) {
                                    boolean status = path.mkdirs();
                                }
                                
                                File uploadedFile = new File(path +"/"+ fileName+".pdf"); 
                                msj += uploadedFile.getAbsolutePath()+"";
                                item.write(uploadedFile);
                                
                                String np = "pad.fn_docadjunto_mant";            
                                String array[] = new String[4];
                                array[0] = iddoc;
                                array[1] = fileName+".pdf";
                                array[2] = dir;
                                array[3] = fileName_1;
                                
                                Vector datos = cdb.EjecutarProcedurePostgres(np, array); 
                            }
                        }
                        msj += "File Uploaded Successfully";
                        request.setAttribute("request", msj);
                        
                    } catch (FileUploadException e) {
                        request.setAttribute("request", "x1 File Upload Failed due to " + e.getMessage());
                    } catch (Exception e) {
                        request.setAttribute("request", "x2 File Upload Failed due to " + e.getMessage());
                    }
                }
        return "pad/uploadfile";
    }  
//FIN SUBIR DOCUMENTOS   
//
//INICIO LISTA ADJUNTOS
    @RequestMapping(value = {"/pad/mant_adjunto_cargar"}, method = RequestMethod.GET)
    public String MantAdjuntoCargar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        
        try {
            String id_doc = request.getParameter("id_doc"); 
            ConeccionDB cn = new ConeccionDB();
                        
            String np = "pad.fn_docadjunto_consulta";
            String array[] = new String[1];
            array[0] = id_doc;
            Vector datos = cn.EjecutarProcedurePostgres(np, array); 
            
            Vector v_tbl_cab_adj =  new Vector();
            v_tbl_cab_adj.add("Item");
            v_tbl_cab_adj.add("Doc. Adjunto");
//            v_tbl_cab_adj.add("Eliminar");
            v_tbl_cab_adj.add("Ver");
            
            Util util_adj = new Util();
            Vector v_temp_adj = new Vector();
            int d = 0; 
            int i = 0 ;
            for(i = 0 ; i<datos.size() ; i++){
                d = d+1;
                Vector datos_v =  (Vector) datos.get(i);
                String id = datos_v.get(0).toString();//id del documento
                String nom_doc = datos_v.get(1).toString();
                String dir = datos_v.get(2).toString();
                String anio = datos_v.get(3).toString();    
                String idn = datos_v.get(4).toString();    
                String nombre = datos_v.get(5).toString();    
//                String btn_elim = "<div class='form-group dropdown'>"
//                                    + "<button type='button' class='btn btn-info' id='elim' onclick='sgd_mant_adjuntos_elim('"+id_doc+"','"+nom_doc+"')'>"
//                                    + "<span class='glyphicon glyphicon-remove'>"
//                                    + "</span>"
//                                    + "</button>"
//                                    + "</div>";
                
                String btn_ver = "<div class='form-group dropdown text-center'>"
                                    + "<button type='button' class='btn btn-info' onclick='window.open(\"http://sgd.senamhi.gob.pe/files/pad/"+anio+"/"+dir+"/"+nom_doc+"\")'>"
                                    + "<span class='glyphicon glyphicon-search'>"
                                    + "</span>"
                                    + "</button>"
                                    + "</div>";
                
                Vector v_adj = new Vector();
                v_adj.add(idn);
                v_adj.add(nombre);
//                v_adj.add(btn_elim);
                v_adj.add(btn_ver);
                v_temp_adj.add(v_adj);
            }
            String tbl_doc_adj = util_adj.tbl(v_tbl_cab_adj, v_temp_adj); 
            if (i == 0){
                request.setAttribute("response","El documento no tiene adjuntos.");
            }else{
                request.setAttribute("response",tbl_doc_adj);
            }        
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return "pad/mant_adjunto_cargar";
    }     
//FIN LISTA ADJUNTOS
//    
//INICIO MANTENIMIENTO MODIFICAR EXPEDIENTE DEL PAD POPUP            
    @RequestMapping(value = {"/pad/mant_expedientes_pad_modifica_popup"}, method = RequestMethod.GET)
    public String MantExpedientePadModificaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","ADICIONAR DOCUMENTO");     

        try {
            
            Date date = new Date();//Fecha de registro del documento (por referencia)
            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
            String fec_doc = formatofec.format(date); 
            
            ConeccionDB cn = new ConeccionDB(); 
            Util util =  new Util();
            
            String id_exp = request.getParameter("id");                         
            
            String np = "pad.fn_expediente_pad_consulta";
                String array[] = new String[1];
                array[0] = id_exp;
                Vector datos = cn.EjecutarProcedurePostgres(np, array);

                String v_id_exp = "";
                String d_fec_recep = "";          
                String d_fecpresc_iniPAD = "";          
                String i_id_etapa = "";        
                String v_id_denunciante = "";      
                String i_id_dependencia = "";      
                String v_id_abogado = "";      
                String v_organo_instr = "";      
                String v_organo_sanc = "";      
                String fecnotif_iniPAD = "";      
                String fecpres_PAD = "";      
                String investigado = "";      
                String estado = "";      
                String uo_organo_instr = "";      
                String uo_organo_sanc = "";      

                for(int i = 0 ; i<datos.size() ; i++){                 
                    Vector datos_v =  (Vector) datos.get(i);
                    v_id_exp = datos_v.get(0).toString();
                    d_fec_recep = datos_v.get(1).toString();
                    d_fecpresc_iniPAD = datos_v.get(7).toString();
                    i_id_etapa = datos_v.get(6).toString();
                    v_id_denunciante = datos_v.get(8).toString();
                    i_id_dependencia = datos_v.get(9).toString();
                    v_id_abogado = datos_v.get(10).toString();
                    v_organo_instr = datos_v.get(11).toString();
                    v_organo_sanc = datos_v.get(12).toString();
                    fecnotif_iniPAD = datos_v.get(13).toString();
                    fecpres_PAD = datos_v.get(14).toString();
                    investigado = datos_v.get(15).toString();
                    estado = datos_v.get(16).toString();
                    uo_organo_instr = datos_v.get(17).toString();
                    uo_organo_sanc = datos_v.get(18).toString();
                }  
            
            request.setAttribute("nroexp", v_id_exp);
            request.setAttribute("fecharecep", d_fec_recep);
            request.setAttribute("fecpresc_iniPAD", d_fecpresc_iniPAD);
            request.setAttribute("fecnotif_iniPAD", fecnotif_iniPAD);
            request.setAttribute("fecpres_PAD", fecpres_PAD);
            request.setAttribute("fecdoc", fec_doc);
            request.setAttribute("estado", estado);
            request.setAttribute("investigado", investigado);
            
//          informaci�n para el combo Etapa
            String etapa = "pad.fn_etapa_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(etapa, array_cbo);
            String cb_etapa = util.contenido_combo(datos_cbo, i_id_etapa);
            request.setAttribute("etapa", cb_etapa);
                        
//            informaci�n combos: denunciante
            String cn_denunciante = "senamhi.fn_destino_consulta";//consulta combo destino                  
            String array_denunciante[] = new String[1];
            array_denunciante[0] = "";
            Vector datos_cbo_denunciante = cn.EjecutarProcedurePostgres(cn_denunciante, array_denunciante); 
            String cb_denunciante = util.contenido_combo(datos_cbo_denunciante, v_id_denunciante);
            request.setAttribute("denunciante", cb_denunciante); 
            
//            informaci�n para el combo Dependencia
            String dependencia = "pad.fn_dependencia_consulta";
            String array_cbo_dependencia[] = new String[1];
            array_cbo_dependencia[0] = "";
            Vector datos_cbo_dependencia = cn.EjecutarProcedurePostgres(dependencia, array_cbo_dependencia);
            String cb_dependencia = util.contenido_combo(datos_cbo_dependencia, i_id_dependencia);
            request.setAttribute("dependencia", cb_dependencia); 
            
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, v_id_abogado);
            request.setAttribute("abogado", cb_abogado);
            
//          informaci�n para el combo �rgano instructor 
            String organo_ins = "pad.fn_organos_directores_consulta";
            String array_cbo_organo_ins[] = new String[1];
            array_cbo_organo_ins[0] = "";
            Vector datos_cbo_organo_ins = cn.EjecutarProcedurePostgres(organo_ins, array_cbo_organo_ins);
            String cb_organo_ins = util.contenido_combo(datos_cbo_organo_ins, v_organo_instr + "_" + uo_organo_instr);
            request.setAttribute("organo_ins", cb_organo_ins);
                        
//          informaci�n para el combo �rgano sancionador
            String organo_san = "pad.fn_organos_directores_consulta";
            String array_cbo_organo_san[] = new String[1];
            array_cbo_organo_san[0] = "";
            Vector datos_cbo_organo_san = cn.EjecutarProcedurePostgres(organo_san, array_cbo_organo_san);
            String cb_organo_san = util.contenido_combo(datos_cbo_organo_san, v_organo_sanc + "_" + uo_organo_sanc);
            request.setAttribute("organo_san", cb_organo_san);           
            
//            informaci�n tipo de documento
            String ntdoc = "sgd.fn_clasifdoc_seriedoc_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_tdoc[] = new String[1];
            array_cbo_tdoc[0] = "90000048";
            Vector datos_cbo_tdoc = cn.EjecutarProcedurePostgres(ntdoc, array_cbo_tdoc);
            String cb_desc_clsfdoc = util.contenido_combo(datos_cbo_tdoc, "");
            request.setAttribute("clsfdoc", cb_desc_clsfdoc);
          
//            informaci�n combos: remite
            String cons_rmte = "pad.fn_destino_consulta";//consulta combo destino                  
            String array_rmte[] = new String[1];
            array_rmte[0] = "";
            Vector datos_cbo_rmte = cn.EjecutarProcedurePostgres(cons_rmte, array_rmte); 
            String cb_rmte = util.contenido_combo(datos_cbo_rmte, "");
            request.setAttribute("remite", cb_rmte); 
            
//            informaci�n combos: destino
            String cons_destino = "pad.fn_destino_consulta";//consulta combo destino                  
            String array_destino[] = new String[1];
            array_destino[0] = "";
            Vector datos_cbo_destino = cn.EjecutarProcedurePostgres(cons_destino, array_destino); 
            String cb_destino = util.contenido_combo(datos_cbo_destino, "");
            request.setAttribute("destino", cb_destino); 
            
//            informaci�n combo: tipo de procedimiento
            String cons_tipo_proced = "pad.fn_tipo_proced_consulta";//consulta combo destino                  
            String array_tipo_proced[] = new String[1];
            array_tipo_proced[0] = "";
            Vector datos_cbo_tipo_proced = cn.EjecutarProcedurePostgres(cons_tipo_proced, array_tipo_proced); 
            String cb_datos_tipo_proced = util.contenido_combo(datos_cbo_tipo_proced, "");
            request.setAttribute("tipo_proced", cb_datos_tipo_proced);
                        
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_expedientes_pad_modifica_popup";
    }   
//FIN MANTENIMIENTO MODIFICAR EXPEDIENTE DEL PAD POPUP
// 
//INICIO CONSULTA EXPEDIENTE DEL PAD POPUP            
    @RequestMapping(value = {"/pad/mant_expedientes_pad_consulta_popup"}, method = RequestMethod.GET)
    public String MantExpedientePadConsultaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","CONSULTA DE EXPEDIENTE");     

        try {
            String nroexp = request.getParameter("id");
                        
            ConeccionDB cn = new ConeccionDB(); 
            Util util =  new Util();
            String np = "pad.fn_expediente_pad_consulta";
                String array[] = new String[1];
                array[0] = nroexp;
                Vector datos = cn.EjecutarProcedurePostgres(np, array);

                String obj_active_form = "";

                String v_id_exp = "";
                String d_fec_recep = "";          
                String d_fecpresc_iniPAD = "";          
                String i_id_etapa = "";                        
                String fecnotif_iniPAD = "";      
                String fecpres_PAD = "";      
                String i_id_estado = "";      

                for(int i = 0 ; i<datos.size() ; i++){                 
                    Vector datos_v =  (Vector) datos.get(i);
                    v_id_exp = datos_v.get(0).toString();
                    d_fec_recep = datos_v.get(1).toString();
                    d_fecpresc_iniPAD = datos_v.get(7).toString();
                    i_id_etapa = datos_v.get(6).toString();
                    fecnotif_iniPAD = datos_v.get(13).toString();
                    fecpres_PAD = datos_v.get(14).toString();
                    i_id_estado = datos_v.get(16).toString();
                }  
                request.setAttribute("nroexp", nroexp); 
                request.setAttribute("fecharecep", d_fec_recep); 
                request.setAttribute("fecpresc_iniPAD", d_fecpresc_iniPAD); 
                request.setAttribute("estado", i_id_estado); 
                
//          informaci�n para el combo Etapa
            String etapa = "pad.fn_etapa_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(etapa, array_cbo);
            String cb_etapa = util.contenido_combo(datos_cbo, i_id_etapa);
            request.setAttribute("etapa", cb_etapa);  
            
//            informaci�n combos: denunciante
            String cn_denunciante = "senamhi.fn_destino_consulta";//consulta combo destino                  
            String array_denunciante[] = new String[1];
            array_denunciante[0] = "";
            Vector datos_cbo_denunciante = cn.EjecutarProcedurePostgres(cn_denunciante, array_denunciante); 
            String cb_denunciante = util.contenido_combo(datos_cbo_denunciante, "");
            request.setAttribute("denunciante", cb_denunciante); 
            
//            informaci�n para el combo Dependencia
            String dependencia = "pad.fn_dependencia_consulta";
            String array_cbo_dependencia[] = new String[1];
            array_cbo_dependencia[0] = "";
            Vector datos_cbo_dependencia = cn.EjecutarProcedurePostgres(dependencia, array_cbo_dependencia);
            String cb_dependencia = util.contenido_combo(datos_cbo_dependencia, "");
            request.setAttribute("dependencia", cb_dependencia);      
            
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, "");
            request.setAttribute("abogado", cb_abogado);
                        
//          informaci�n para el combo �rgano instructor / �rgano sancionador 
            String organo_ins = "pad.fn_organos_directores_consulta";
            String array_cbo_organo_ins[] = new String[1];
            array_cbo_organo_ins[0] = "";
            Vector datos_cbo_organo_ins = cn.EjecutarProcedurePostgres(organo_ins, array_cbo_organo_ins);
            String cb_organo_ins = util.contenido_combo(datos_cbo_organo_ins, "");
            request.setAttribute("organo", cb_organo_ins);    
            
//            informaci�n tipo de documento
            String ntdoc = "sgd.fn_clasifdoc_seriedoc_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_tdoc[] = new String[1];
            array_cbo_tdoc[0] = "90000048";
            Vector datos_cbo_tdoc = cn.EjecutarProcedurePostgres(ntdoc, array_cbo_tdoc);
            String cb_desc_clsfdoc = util.contenido_combo(datos_cbo_tdoc, "");
            request.setAttribute("clsfdoc", cb_desc_clsfdoc);            
            
//            informaci�n combos: remite y destino
            String cons_rmte = "senamhi.fn_destino_consulta";//consulta combo remite y destino                 
            String array_rmte[] = new String[1];
            array_rmte[0] = "";
            Vector datos_cbo_rmte = cn.EjecutarProcedurePostgres(cons_rmte, array_rmte); 
            String cb_rmte = util.contenido_combo(datos_cbo_rmte, "");
            request.setAttribute("persona", cb_rmte);
            
//            informaci�n combo investigado
//            String cons_inv = "pad.fn_organo_consulta";//consulta investigado(s) del expediente
//            String array_inv[] = new String[1];
//            array_inv[0] = "";
//            Vector datos_cbo_inv = cn.EjecutarProcedurePostgres(cons_inv, array_inv);
//            String cb_inv = util.contenido_combo(datos_cbo_inv, "");
//            request.setAttribute("investigado", cb_organo_ins);
          
//            informaci�n combo: tipo de procedimiento
            String cons_tipo_proced = "pad.fn_tipo_proced_consulta";//consulta combo destino                  
            String array_tipo_proced[] = new String[1];
            array_tipo_proced[0] = "";
            Vector datos_cbo_tipo_proced = cn.EjecutarProcedurePostgres(cons_tipo_proced, array_tipo_proced); 
            String cb_datos_tipo_proced = util.contenido_combo(datos_cbo_tipo_proced, "");
            request.setAttribute("tipo_proced", cb_datos_tipo_proced);       
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_expedientes_pad_consulta_popup";
    }   
//FIN CONSULTA EXPEDIENTE DEL PAD POPUP
// 
//INICIO LISTA DOCUMENTOS POR EXPEDIENTE TABLA        
    @RequestMapping(value = {"/pad/mant_expedientes_pad_docs_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryExpedientesPadDocsTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws ParseException {
            
            String id = request.getParameter("nroexp");
                       
//            ConeccionDB cn =  new ConeccionDB();            

//            String np = "pad.fn_expediente_pad_docs_consulta";
//            String array[] = new String[1];
//            array[0] = id;
//            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);
//
//            Vector v_temp = new Vector();
//            for(int i = 0 ; i<v_datos.size() ; i++){
//                Vector vss =  (Vector) v_datos.get(i);
//                String iddoc = vss.get(0).toString();
//                String tipodoc = vss.get(1).toString();
//                String fechadoc = vss.get(3).toString();
//                String asunto = vss.get(7).toString();                
//                String remite = vss.get(5).toString();                
//                String destino = vss.get(6).toString();                
//                
//                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_modifica_popup(\\\""+iddoc+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
//                
//                Vector vv = new Vector();
//                vv.add(tipodoc);
//                vv.add(fechadoc);
//                vv.add(asunto);
//                vv.add(remite);
//                vv.add(destino);
//                vv.add(btn);
//                v_temp.add(vv);
//            }
//            
//            Util util = new Util();
//            String json = util.vector2json(v_temp);
//            Vector vc_tbl = new Vector();
//            Vector sv =  new Vector();
//            sv.add("bScrollCollapse");sv.add("true");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("sScrollY");sv.add("'90%'");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("aoColumns");sv.add("["
//                                    + "{'sTitle':'DOCUMENTO'} , "
//                                    + "{'sTitle':'FECHA DOC.'} , "
//                                    + "{'sTitle':'ASUNTO'} , "
//                                    + "{'sTitle':'REMITE'} , "
//                                    + "{'sTitle':'DESTINO'} , "
////                                    + "{'sTitle':'F.RESPUESTA'} , "
//                                    + "{'sTitle':'EDITAR'}  "
//                                    + "]");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("aaData");sv.add(json);vc_tbl.add(sv);sv =  new Vector();
//        //      sv.add("aoColumnDefs");sv.add("[{'sClass':'center','aTargets':[0,1,4,5,6]},{'aTargets':[ 10 ],'bVisible': false,'bSearchable': false}]");vc_tbl.add(sv);sv =  new Vector();
//            //boton de excel
//            sv.add("dom");sv.add("'Bfrtip'");vc_tbl.add(sv);sv =  new Vector();
////            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm' }]");vc_tbl.add(sv);sv =  new Vector();
//            ////Pintar de rojo el registro si no t.iene datos
////            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
////                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
////                          "}";
////            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();
//
//            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_exp_docs'></table>";
//            String tbl = util.datatable("c_tbl_exp_docs",vc_tbl);            
//            request.setAttribute("response", tbl_html + tbl);

        String btn_ver = "";
        String var_request = "";
        Util util = new Util();
        try {                    
            ConeccionDB cdb = new ConeccionDB(); 
             String np = "pad.fn_expediente_pad_docs_consulta";
            String array_docs[] = new String[1];
            array_docs[0] = id;
            Vector v_tbl_data_docs = cdb.EjecutarProcedurePostgres(np, array_docs);

            Vector v_tbl_cab =  new Vector();
            v_tbl_cab.add("N");
            v_tbl_cab.add("DOCUMENTO");
            v_tbl_cab.add("FECHA DOC.");
            v_tbl_cab.add("ASUNTO");
            v_tbl_cab.add("FECHA RPTA.");
            v_tbl_cab.add("VER");

            Vector v_temp = new Vector();
            int k;
            for(k = 0 ; k<v_tbl_data_docs.size() ; k++){
                Vector vss =  (Vector) v_tbl_data_docs.get(k);
                String iddoc = vss.get(0).toString();
                String tipodoc = vss.get(1).toString();
                String fechadoc = vss.get(3).toString();
                String asunto = vss.get(7).toString();                
                String fec_rpta = vss.get(11).toString();                
                String idn_doc = vss.get(12).toString();

                btn_ver = "<div class='form-group dropdown text-center' >"
                                + "<button type='button' class='btn btn-info' id='ver' onclick='pad_mant_adjuntos_cargar(\""+iddoc+"\"), pad_mant_doc_detalle(\""+iddoc+"\")'>"
                                + "<span class='glyphicon glyphicon-search'>"
                                + "</span>"
                                + "</button>"
                                + "</div>";  
                
                Vector vv_d = new Vector();
                vv_d.add(idn_doc);
                vv_d.add(tipodoc);
                vv_d.add(fechadoc);                
                vv_d.add(asunto);
                vv_d.add(fec_rpta);
                vv_d.add(btn_ver);
                v_temp.add(vv_d);                
                }
            
        String tbl_deriva = util.tbl(v_tbl_cab, v_temp);              
        request.setAttribute("response",tbl_deriva);           
        
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
            return "pad/mant_expedientes_pad_docs_tbl";
	}
//FIN LISTA DOCUMENTOS POR EXPEDIENTE TABLA         
//  
//INICIO DOCUMENTOS DETALLE    
    @RequestMapping(value = {"/pad/mant_doc_detalle"}, method = RequestMethod.GET)
    public String MantDocDetalle(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {   
        
        String id_doc = request.getParameter("id_doc"); 
        String var_request = "";
        try {
            ConeccionDB cn = new ConeccionDB();   
            Util util =  new Util();
                                   
            String np = "pad.fn_expediente_doc_pad_consulta";
            String array[] = new String[1];
            array[0] = id_doc;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            var_request = new Util().vector2json(datos);
                   
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        request.setAttribute("response", var_request);
        return "pad/mant_doc_detalle";
    }     
//FIN DOCUMENTOS DETALLE
//
//INICIO MANTENIMIENTO NUEVO INVESTIGADO POPUP            
    @RequestMapping(value = {"/pad/mant_investigado_popup"}, method = RequestMethod.GET)
    public String MantInvestigadoPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","ADICIONAR INVESTIGADO");     

        try {
            String id = request.getParameter("id");
            request.setAttribute("nroexp", id);
            
            ConeccionDB cn = new ConeccionDB(); 
            Util util =  new Util();
            
//          informaci�n para el combo Investigado
            String inv = "pad.fn_organo_consulta";
            String array_cbo_inv[] = new String[1];
            array_cbo_inv[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(inv, array_cbo_inv);
            String cb_investigado = util.contenido_combo(datos_cbo, "");
            request.setAttribute("investigado", cb_investigado);
            
//          informaci�n para el combo Cargo
            String cargo = "pad.fn_cargo_consulta";
            String array_cbo_cargo[] = new String[1];
            array_cbo_cargo[0] = "";
            Vector datos_cbo_cargo = cn.EjecutarProcedurePostgres(cargo, array_cbo_cargo);
            String cb_cargo = util.contenido_combo(datos_cbo_cargo, "");
            request.setAttribute("cargo", cb_cargo);
            
//          informaci�n para el combo Norma
            String norma = "pad.fn_norma_consulta";
            String array_cbo_norma[] = new String[1];
            array_cbo_norma[0] = "";
            Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
            String cb_norma = util.contenido_combo(datos_cbo_norma, "");
            request.setAttribute("norma", cb_norma);
            
//          informaci�n para el combo Falta
            String falta = "pad.fn_literal_investigado_consulta";
            String array_cbo_falta[] = new String[1];
            array_cbo_falta[0] = "";
            Vector datos_cbo_falta = cn.EjecutarProcedurePostgres(falta, array_cbo_falta);
            String cb_falta = util.contenido_combo(datos_cbo_falta, "");
            request.setAttribute("falta", cb_falta);
                      
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_investigado_popup";
    }   
//FIN MANTENIMIENTO NUEVO INVESTIGADO POPUP
// 
//INICIO CARGAR COMBO FALTAS
//    @RequestMapping(value = {"/pad/mant_falta_cargar_cbo"}, method = RequestMethod.GET)
//    public String MantFaltaCargarCbo(HttpServletRequest request, HttpServletResponse response, ModelMap model)
//            throws ServletException, IOException { 
//        String var_request = "";
//        try {
//            String id_normajur = request.getParameter("id_normajur"); 
//            
//            ConeccionDB cn = new ConeccionDB();   
//            
//            String np = "pad.fn_falta_normajur_consulta";
//            String array[] = new String[1];
//            array[0] = id_normajur;
//            Vector datos = cn.EjecutarProcedurePostgres(np, array);
//            var_request = new Util().contenido_combo(datos,"1");
//            
//        } catch (Exception ex) {
//            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
//        }        
//        request.setAttribute("response", var_request);        
//        return "pad/mant_falta_cargar_cbo";
//    } 
//FIN CARGAR COMBO FALTAS   
//    
//INICIO NUEVO INVESTIGADO GUARDAR    
@RequestMapping(value = {"/pad/mant_investigado_guardar"}, method = RequestMethod.GET)
    public String MantInvestigadoGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id_exp = request.getParameter("idexp");        
        String investigado = request.getParameter("investigado");        
        String cargo = request.getParameter("cargo");        
        String observacion = request.getParameter("observacion");        
        String faltas = request.getParameter("faltas");    
        if (faltas.equals("null")){
            faltas = "";
        }
        
        String var_request = "";        
        try {                    
            ConeccionDB cdb = new ConeccionDB(); 
            String np = "pad.fn_investigado_mant";
            String array[] = new String[5];
            array[0] = investigado;
            array[1] = cargo;            
            array[2] = id_exp;            
            array[3] = observacion;            
            array[4] = faltas;     
            
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);

            var_request = new Util().vector2json(datos);
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_investigado_guardar";
    }
//FIN NUEVO INVESTIGADO GUARDAR     
//
//INICIO CONSULTA INVESTIGADO POPUP            
    @RequestMapping(value = {"/pad/mant_investigado_consulta_popup"}, method = RequestMethod.GET)
    public String MantInvestigadoConsultaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","CONSULTA DE INVESTIGADOS");     

        try {
            String id = request.getParameter("id");
            request.setAttribute("nroexp", id);
            
//            Calendar c = Calendar.getInstance();//Anio actual para el registro del expediente           
//            String anio = Integer.toString(c.get(Calendar.YEAR));
//            
//            Date date = new Date();//Fecha de registro del documento (por referencia)
//            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
//            String fecharecep = formatofec.format(date);        
//                        
//            //Fecha del documeto, por defecto el d�a actual            
//            DateFormat fecDoc = new SimpleDateFormat("dd/MM/yyyy");
//            String fec_doc = fecDoc.format(date); 
            
            //Fecha de prescripci�n del inicio del PAD
//            Date nuevaFecha = new Date();
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DAY_OF_YEAR, 365);
//            nuevaFecha = cal.getTime();
//            String fecpresc_iniPAD = formatofec.format(nuevaFecha); 
            
            ConeccionDB cn = new ConeccionDB(); 
            Util util =  new Util();
            
//          informaci�n para el combo Investigado
            String etapa = "pad.fn_organo_consulta";
            String array_cbo_inv[] = new String[1];
            array_cbo_inv[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(etapa, array_cbo_inv);
            String cb_investigado = util.contenido_combo(datos_cbo, "");
            request.setAttribute("investigado", cb_investigado);
            
//          informaci�n para el combo Cargo
            String abogado = "pad.fn_cargo_consulta";
            String array_cbo_cargo[] = new String[1];
            array_cbo_cargo[0] = "";
            Vector datos_cbo_cargo = cn.EjecutarProcedurePostgres(abogado, array_cbo_cargo);
            String cb_cargo = util.contenido_combo(datos_cbo_cargo, "");
            request.setAttribute("cargo", cb_cargo);       
            
////          informaci�n para el combo Falta
//            String falta = "pad.fn_literal_investigado_consulta";
//            String array_cbo_falta[] = new String[1];
//            array_cbo_falta[0] = "";
//            Vector datos_cbo_falta = cn.EjecutarProcedurePostgres(falta, array_cbo_falta);
//            String cb_falta = util.contenido_combo(datos_cbo_falta, "");
//            request.setAttribute("falta", cb_falta);

//          informaci�n para el combo Norma
//            String norma = "pad.fn_norma_consulta";
//            String array_cbo_norma[] = new String[1];
//            array_cbo_norma[0] = "";
//            Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
//            String cb_norma = util.contenido_combo(datos_cbo_norma, "");
//            request.setAttribute("norma", cb_norma);            

//          informaci�n para el combo Falta
            String falta = "pad.fn_literal_investigado_consulta";
            String array_cbo_falta[] = new String[1];
            array_cbo_falta[0] = "";
            Vector datos_cbo_falta = cn.EjecutarProcedurePostgres(falta, array_cbo_falta);
            String cb_falta = util.contenido_combo(datos_cbo_falta, "");
            request.setAttribute("falta", cb_falta);            
            
//            informaci�n combo Sanci�n
            String sancion = "pad.fn_sancion_consulta";//consulta combo destino                  
            String array_sancion[] = new String[1];
            array_sancion[0] = "";
            Vector datos_cbo_sancion = cn.EjecutarProcedurePostgres(sancion, array_sancion); 
            String cb_sancion = util.contenido_combo(datos_cbo_sancion, "");
            request.setAttribute("sancion", cb_sancion); 
            
//            informaci�n combo medida cautelar
            String medcaut = "pad.fn_medida_caut_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_medcaut[] = new String[1];
            array_cbo_medcaut[0] = "";
            Vector datos_cbo_medcaut = cn.EjecutarProcedurePostgres(medcaut, array_cbo_medcaut);
            String cb_desc_medcaut = util.contenido_combo(datos_cbo_medcaut, "");
            request.setAttribute("medcaut", cb_desc_medcaut);
          
//            informaci�n combo recurso
            String recurso = "pad.fn_recurso_consulta";//combo Tipo de Documentos por Unidad Org�nica
            String array_cbo_recurso[] = new String[1];
            array_cbo_recurso[0] = "";
            Vector datos_cbo_recurso = cn.EjecutarProcedurePostgres(recurso, array_cbo_recurso);
            String cb_desc_recurso = util.contenido_combo(datos_cbo_recurso, "");
            request.setAttribute("recurso", cb_desc_recurso);
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_investigado_consulta_popup";
    }   
//FIN CONSULTA INVESTIGADO POPUP
//  
//INICIO LISTA DOCUMENTOS POR EXPEDIENTE TABLA        
    @RequestMapping(value = {"/pad/mant_investigados_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryInvestigadosTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws ParseException {
            
        String id = request.getParameter("nroexp");
                       
        String btn_ver = "";
        String var_request = "";
        Util util = new Util();
        try {                    
            ConeccionDB cdb = new ConeccionDB(); 
             String np = "pad.fn_investigados_consulta";
            String array_docs[] = new String[1];
            array_docs[0] = id;
            Vector v_tbl_data_docs = cdb.EjecutarProcedurePostgres(np, array_docs);

            Vector v_tbl_cab =  new Vector();
            v_tbl_cab.add("COD.EMPL.");
            v_tbl_cab.add("INVESTIGADO");
            v_tbl_cab.add("CARGO");
            v_tbl_cab.add("VER");

            Vector v_temp = new Vector();
            int k;
            for(k = 0 ; k<v_tbl_data_docs.size() ; k++){
                Vector vss =  (Vector) v_tbl_data_docs.get(k);
                String idinv = vss.get(0).toString();
                String investigado = vss.get(1).toString();
                String cargo = vss.get(2).toString();

                btn_ver = "<div class='form-group dropdown text-center' >"
                                + "<button type='button' class='btn btn-info' id='ver' onclick='pad_mant_investigado_detalle(\""+idinv+"\");'>"
                                + "<span class='glyphicon glyphicon-search'>"
                                + "</span>"
                                + "</button>"
                                + "</div>";  
                
                Vector vv_d = new Vector();
                vv_d.add(idinv);
                vv_d.add(investigado);
                vv_d.add(cargo);
                vv_d.add(btn_ver);
                v_temp.add(vv_d);                
                }
            
        String tbl_investigado = util.tbl(v_tbl_cab, v_temp);              
        request.setAttribute("response",tbl_investigado);           
        
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
            return "pad/mant_investigados_tbl";
	}
//FIN LISTA DOCUMENTOS POR EXPEDIENTE TABLA         
//
//INICIO DOCUMENTOS DETALLE    
    @RequestMapping(value = {"/pad/mant_investigado_detalle"}, method = RequestMethod.GET)
    public String MantInvestigadoDetalle(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {   
        
        String id_inv = request.getParameter("investigado"); 
        String var_request = "";
        try {
            ConeccionDB cn = new ConeccionDB();   
            Util util =  new Util();
                                   
            String np = "pad.fn_investigados_exp_consulta";
            String array[] = new String[1];
            array[0] = id_inv;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            var_request = new Util().vector2json(datos);
                   
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        request.setAttribute("response", var_request);
        return "pad/mant_investigado_detalle";
    }     
//FIN DOCUMENTOS DETALLE
//   
//INICIO NUEVO INVESTIGADO GUARDAR    
@RequestMapping(value = {"/pad/mant_investigado_modificar_guardar"}, method = RequestMethod.GET)
    public String MantInvestigadoModificaGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id_exp = request.getParameter("idexp");        
        String investigado = request.getParameter("investigado");        
        String cargo = request.getParameter("cargo");        
        String observacion = request.getParameter("observacion");        
        String faltas = request.getParameter("faltas");        
        String sancion = request.getParameter("sancion");         
        String dias = request.getParameter("dias");        
        String medcautelar = request.getParameter("medcautelar");                  
        if (medcautelar.equals("null")){
            medcautelar = "";
        }
        String recurso = request.getParameter("recurso");        
        if (recurso.equals("null")){
            recurso = "";
        }
        String var_request = "";

        try {                    
            ConeccionDB cdb = new ConeccionDB();
            String np = "pad.fn_investigado_modifica";
            String array[] = new String[9];
            array[0] = investigado;
            array[1] = cargo;
            array[2] = id_exp;
            array[3] = observacion;
            array[4] = faltas;
            array[5] = sancion;
            array[6] = dias;
            array[7] = medcautelar;
            array[8] = recurso;
            
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);

            var_request = new Util().vector2json(datos);
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_investigado_modificar_guardar";
    }
//FIN NUEVO INVESTIGADO GUARDAR     
//
//INICIO ASIGNA ABOGADO POPUP
    @RequestMapping(value = {"/pad/mant_asigna_abogado_popup"}, method = RequestMethod.GET)
    public String MantAsignaAbogadoConsultaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","ASIGNAR ABOGADO");
        String array_expediente = request.getParameter("array_expediente");
        int x = 0;
        try {
            String exp = "";
            String nro_exp = "";
            String fec_rec = "";
            String etapa = "";
            String cad_id_exp = "";
            
            
            String[] array_exp = array_expediente.split(",");
            
            String tbl_exp = "<table class='table table-striped'>" +
                             "<tr class='success'>" +
                             "  <td>ITEM</td>" +
                             "  <td>N� EXPEDIENTE</td>" +
                             "  <td>FECHA RECEP.(ORH)</td>" +
                             "  <td>ETAPA</td>" +
                             "</tr>";
            String id_exp_array = "";
            for (x = 0; x < array_exp.length; x++){
                exp = array_exp[x];
                String[] array_elem = exp.split("_");
                nro_exp = array_elem [0];
                fec_rec = array_elem [1];
                etapa = array_elem [2];
                
                tbl_exp += "<tr>" +
                                "  <td class='text-center'>"+x+"</td>" +
                                "  <td>"+nro_exp+"</td>" +
                                "  <td>"+fec_rec+"</td>" +
                                "  <td>"+etapa+ "</td>" +
                                "</tr>";
                cad_id_exp += nro_exp + ",";
            }
            cad_id_exp = cad_id_exp.replace(id_exp_array, "");
            cad_id_exp = id_exp_array + cad_id_exp;
            tbl_exp += "</table>";
            request.setAttribute("tbl_exp", tbl_exp);
            request.setAttribute("cad_id_exp", cad_id_exp);  
            
            ConeccionDB cn = new ConeccionDB();
            Util util =  new Util();
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, "");
            request.setAttribute("abogado", cb_abogado);        
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_asigna_abogado_popup";
    }
//FIN ASIGNA ABOGADO POPUP
//  
//INICIO ASIGNA ABOGADO GUARDAR    
@RequestMapping(value = {"/pad/mant_asigna_abogado_modificar_guardar"}, method = RequestMethod.GET)
    public String MantAsignaAbogadoModificaGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String cadidexp = request.getParameter("cadidexp");        
        String abogado = request.getParameter("abogado");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_asigna_abogado_modifica";
            String array[] = new String[2];
            array[0] = cadidexp;
            array[1] = abogado; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_asigna_abogado_modificar_guardar";
    }
//FIN ASIGNA ABOGADO GUARDAR     
// 
//INICIO LISTA DE EXPEDIENTE DEL PAD BASE        
    @RequestMapping(value = {"/pad/mant_buscar"}, method = RequestMethod.GET)
	public String MantBuscar(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","BUSCAR EXPEDIENTES");
        
            try {
            ConeccionDB cn = new ConeccionDB();               
            Util util =  new Util();
//          informaci�n para el combo periodo (a�o)
            String ne = "pad.fn_anio_exp_consulta";
            String array_cbo_anio[] = new String[1];
            array_cbo_anio[0] = "";
            Vector datos_cbo_anio = cn.EjecutarProcedurePostgres(ne, array_cbo_anio);            
            String cb_anio = util.contenido_combo(datos_cbo_anio, "");
            request.setAttribute("anio", cb_anio);  
            
            String clsdoc = "sgd.fn_clasifdoc_seriedoc_consulta";
            String array_cbo_clsdoc[] = new String[1];
            array_cbo_clsdoc[0] = "90000048";
            Vector datos_cbo_clsdoc = cn.EjecutarProcedurePostgres(clsdoc, array_cbo_clsdoc);            
            String cb_clsdoc = util.contenido_combo(datos_cbo_clsdoc, "");
            request.setAttribute("cb_clsdoc", cb_clsdoc);
            
            Calendar c = Calendar.getInstance();//Anio actual para el registro del expediente
            
            Date date = new Date();//Fecha de registro del documento (por referencia)
            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
            String hoy = formatofec.format(date);
            
            request.setAttribute("hoy", hoy);
            
//Fecha de un mes antes
            Date nuevaFecha = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 365);
            nuevaFecha = cal.getTime();
            String fec_mes = formatofec.format(nuevaFecha); 
            
            request.setAttribute("fec_mes", fec_mes);
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_buscar";
	}
//FIN LISTA DE EXPEDIENTE DEL PAD BASE
//
//INICIO BUSCAR TABLA        
    @RequestMapping(value = {"/pad/mant_buscar_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryBuscarTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String nroexp = request.getParameter("nroexp");
            String anio = request.getParameter("anio");
            String fecini = request.getParameter("fecini");
            String fecfin = request.getParameter("fecfin");
            String fecinipad = request.getParameter("fecinipad");
            String fecfinpad = request.getParameter("fecfinpad");
            
            ConeccionDB cn =  new ConeccionDB();
            String np = "pad.fn_buscar_consulta";
            String array[] = new String[6];
            array[0] = nroexp;
            array[1] = anio;
            array[2] = fecini;
            array[3] = fecfin;
            array[4] = fecinipad;
            array[5] = fecfinpad;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String nro_exp = vss.get(0).toString();
                String fecharecep = vss.get(1).toString();
                String etapa = vss.get(2).toString();
                String estado = vss.get(4).toString();
                String fec_presipad = vss.get(7).toString();
                String fec_prespad = vss.get(14).toString();
                 
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+nro_exp+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i+1);
                vv.add(nro_exp);
                vv.add(fecharecep);
                vv.add(fec_presipad);
                vv.add(fec_prespad);
                vv.add(etapa);
                vv.add(estado);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'80%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["
                                    + "{'sTitle':'ITEM'} , "
                                    + "{'sTitle':'N� EXPEDIENTE'} , "
                                    + "{'sTitle':'FECHA RECEP. ORH'} , "
                                    + "{'sTitle':'FECHA PRESCR. INIC.PAD'} , "
                                    + "{'sTitle':'FECHA PRESCR. PAD'} , "
                                    + "{'sTitle':'ETAPA'} , "
                                    + "{'sTitle':'ESTADO'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4', download:'open' },"
                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            String tbl_html = "<table border='1' class='table table-striped table-bordered table-responsive-sm text-center' id='c_tbl_buscar_exp'></table>";
            String tbl = util.datatable("c_tbl_buscar_exp",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_buscar_tbl";
	}
//FIN BUSCAR TABLA
// 
//INICIO REPORTE POR       
    @RequestMapping(value = {"/pad/mant_reporte_por"}, method = RequestMethod.GET)
	public String MantReporte1(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE DE EXPEDIENTES POR");
        
            try {
            ConeccionDB cn = new ConeccionDB();               
            Util util =  new Util();
            
//          informaci�n para el combo Etapa
            String etapa = "pad.fn_etapa_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(etapa, array_cbo);
            String cb_etapa = util.contenido_combo(datos_cbo, "");
            request.setAttribute("etapa", cb_etapa);
            
//          informaci�n para el combo Estado
            String estado = "pad.fn_estado_consulta";
            String array_estado[] = new String[1];
            array_estado[0] = "";
            Vector datos_estado = cn.EjecutarProcedurePostgres(estado, array_estado);
            String cb_estado = util.contenido_combo(datos_estado, "");
            request.setAttribute("estado", cb_estado);
            
//          informaci�n para el combo Abogado
            String abogado = "pad.fn_abogado_consulta";
            String array_cbo_abogado[] = new String[1];
            array_cbo_abogado[0] = "";
            Vector datos_cbo_abogado = cn.EjecutarProcedurePostgres(abogado, array_cbo_abogado);
            String cb_abogado = util.contenido_combo(datos_cbo_abogado, "");
            request.setAttribute("abogado", cb_abogado);
                        
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_por";
	}
//FIN REPORTE POR
//
//INICIO REPORTE DE EXPEDIENTES POR TABLA        
    @RequestMapping(value = {"/pad/mant_rep_por_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryRepPorTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String etapa = request.getParameter("etapa");    
            String estado = request.getParameter("estado");    
            String abogado = request.getParameter("abogado");   
            String falta = request.getParameter("falta");   
            String sancion = request.getParameter("sancion");   
            String fecini = request.getParameter("fecini");    
            String fecfin = request.getParameter("fecfin");    
            String fecinipad = request.getParameter("fecinipad");    
            String fecfinpad = request.getParameter("fecfinpad");    
            
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_rep_por_consulta";
            String array[] = new String[7];
            array[0] = etapa;
            array[1] = estado;
            array[2] = abogado;
            array[3] = fecini;
            array[4] = fecfin;
            array[5] = fecinipad;
            array[6] = fecfinpad;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String nro_exp = vss.get(0).toString();
                String fecharecep = vss.get(1).toString();
                String v_etapa = vss.get(2).toString();
                String v_estado = vss.get(4).toString();
                String v_abogado = vss.get(5).toString();
                String fec_presipad = vss.get(7).toString();
                String fec_prespad = vss.get(14).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+nro_exp+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i+1);
                vv.add(nro_exp);
                vv.add(fecharecep);
                vv.add(fec_presipad);
                vv.add(fec_prespad);
                vv.add(v_etapa);
                vv.add(v_estado);
                vv.add(v_abogado);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'ITEM'} , "
                                    + "{'sTitle':'N� EXPEDIENTE'} , "
                                    + "{'sTitle':'FECHA RECEP. ORH'} , "
                                    + "{'sTitle':'FECHA PRESCR. INIC.PAD'} , "
                                    + "{'sTitle':'FECHA PRESCR. PAD'} , "
                                    + "{'sTitle':'ETAPA'} , "
                                    + "{'sTitle':'ESTADO'} , "
                                    + "{'sTitle':'ABOGADO'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_reporte_por'></table>";
            String tbl = util.datatable("c_tbl_reporte_por",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_rep_por_tbl";
	}
//FIN REPORTE DE EXPEDIENTES POR TABLA
//
//INICIO REPORTE POR FALTA
    @RequestMapping(value = {"/pad/mant_reporte_falta"}, method = RequestMethod.GET)
	public String MantReporteFalta(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE DE EXPEDIENTES POR FALTA");
        
            try {
            ConeccionDB cn = new ConeccionDB();               
            Util util =  new Util();
                        
//          informaci�n para el combo faltas
            String faltas = "pad.fn_literal_investigado_consulta";
            String array_cbo_faltas[] = new String[1];
            array_cbo_faltas[0] = "";
            Vector datos_cbo_faltas = cn.EjecutarProcedurePostgres(faltas, array_cbo_faltas);
            String cb_faltas = util.contenido_combo(datos_cbo_faltas, "");
            request.setAttribute("falta", cb_faltas);           
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_falta";
	}
//FIN REPORTE POR FALTA
//
//INICIO REPORTE POR FALTA TABLA
    @RequestMapping(value = {"/pad/mant_reporte_falta_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryReporteFaltaTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String falta = request.getParameter("falta"); 
            
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_rep_falta_consulta";
            String array[] = new String[1];
            array[0] = falta;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String nro_exp = vss.get(0).toString();
                String v_investigado = vss.get(1).toString();
                String v_falta = vss.get(2).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+nro_exp+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i+1);
                vv.add(nro_exp);
                vv.add(v_investigado);
                vv.add(v_falta);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'ITEM'} , "
                                    + "{'sTitle':'N� EXPEDIENTE'} , "
                                    + "{'sTitle':'INVESTIGADO'} , "
                                    + "{'sTitle':'FALTA'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_reporte_por'></table>";
            String tbl = util.datatable("c_tbl_reporte_por",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_reporte_falta_tbl";
	}
//FIN REPORTE POR FALTA TABLA
//
//INICIO REPORTE POR SANCION
    @RequestMapping(value = {"/pad/mant_reporte_sancion"}, method = RequestMethod.GET)
	public String MantReporteSancion(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE DE EXPEDIENTES POR SANCI�N");
        
            try {
            ConeccionDB cn = new ConeccionDB();               
            Util util =  new Util();
            
//            informaci�n combo Sanci�n
            String sancion = "pad.fn_sancion_consulta";//consulta combo sanci�n                  
            String array_sancion[] = new String[1];
            array_sancion[0] = "";
            Vector datos_cbo_sancion = cn.EjecutarProcedurePostgres(sancion, array_sancion); 
            String cb_sancion = util.contenido_combo(datos_cbo_sancion, "");
            request.setAttribute("sancion", cb_sancion);
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_sancion";
	}
//FIN REPORTE POR SANCION
//
//INICIO REPORTE POR SANCION TABLA
    @RequestMapping(value = {"/pad/mant_reporte_sancion_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryReporteSancionTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String sancion = request.getParameter("sancion"); 
            
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_rep_sancion_consulta";
            String array[] = new String[1];
            array[0] = sancion;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String nro_exp = vss.get(0).toString();
                String v_investigado = vss.get(1).toString();
                String v_sancion = vss.get(2).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+nro_exp+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i+1);
                vv.add(nro_exp);
                vv.add(v_investigado);
                vv.add(v_sancion);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'ITEM'} , "
                                    + "{'sTitle':'N� EXPEDIENTE'} , "
                                    + "{'sTitle':'INVESTIGADO'} , "
                                    + "{'sTitle':'SANCION'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered  text-center' id='c_tbl_reporte_por'></table>";
            String tbl = util.datatable("c_tbl_reporte_por",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_reporte_sancion_tbl";
	}
//FIN REPORTE POR SANCION TABLA        
//        
//INICIO LISTA DE EXPEDIENTE DEL PAD BASE        
    @RequestMapping(value = {"/pad/mant_reporte_graf1"}, method = RequestMethod.GET)
	public String MantReporteGraf1(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE GRAFICO DE EXPEDIENTES POR ETAPA");
        
        try {
            Calendar c = Calendar.getInstance();//Anio actual para el registro del expediente
            
            Date date = new Date();//Fecha de registro del documento (por referencia)
            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
            String hoy = formatofec.format(date);
            
            request.setAttribute("hoy", hoy);
            
//Fecha de un mes antes
            Date nuevaFecha = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            nuevaFecha = cal.getTime();
            String fec_mes = formatofec.format(nuevaFecha); 
            
            request.setAttribute("fec_mes", fec_mes);
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_graf1";
	}
//FIN LISTA DE EXPEDIENTE DEL PAD BASE
//  
//INICIO REPORTE POR PERIODO GR�FICO
@RequestMapping(value = {"/pad/mant_reporte_grafico1"}, method = RequestMethod.GET)
    public String MantReporteGrafico1(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {   
          
        String f_ini = request.getParameter("f_ini");   
        String f_fin = request.getParameter("f_fin");   
        String var_request = "";
        try {
            ConeccionDB cn = new ConeccionDB();
            String np = "pad.fn_rep_graf1_consulta";
            String array[] = new String[2];
            array[0] = f_ini;
            array[1] = f_fin;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);                        
            var_request = new Util().vector2json(datos); 
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        request.setAttribute("response", var_request);
        
        return "pad/mant_reporte_grafico1";
    }
//FIN REPORTE POR PERIODO GR�FICO 
// 
//INICIO LISTA DE EXPEDIENTE DEL PAD BASE        
    @RequestMapping(value = {"/pad/mant_reporte_abogado_graf"}, method = RequestMethod.GET)
	public String MantReporteAbogadoGraf(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE GRAFICO DE EXPEDIENTES POR ABOGADO");
        
        try {
            Calendar c = Calendar.getInstance();//Anio actual para el registro del expediente
            
            Date date = new Date();//Fecha de registro del documento (por referencia)
            DateFormat formatofec = new SimpleDateFormat("dd/MM/yyyy");
            String hoy = formatofec.format(date);
            
            request.setAttribute("hoy", hoy);
            
//Fecha de un mes antes
            Date nuevaFecha = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            nuevaFecha = cal.getTime();
            String fec_mes = formatofec.format(nuevaFecha); 
            
            request.setAttribute("fec_mes", fec_mes);
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_abogado_graf";
	}
//FIN LISTA DE EXPEDIENTE DEL PAD BASE
// 
//INICIO REPORTE POR ABOGADO GR�FICO
@RequestMapping(value = {"/pad/mant_reporte_abogado_grafico"}, method = RequestMethod.GET)
    public String MantReporteAbogadoGrafico(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {   
          
        String f_ini = request.getParameter("f_ini");   
        String f_fin = request.getParameter("f_fin");   
        String var_request = "";
        try {
            ConeccionDB cn = new ConeccionDB();
            String np = "pad.fn_rep_abogado_graf_consulta";
            String array[] = new String[2];
            array[0] = f_ini;
            array[1] = f_fin;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);                        
            var_request = new Util().vector2json(datos); 
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        request.setAttribute("response", var_request);
        
        return "pad/mant_reporte_abogado_grafico";
    }
//FIN REPORTE POR ABOGADO GR�FICO 
//      
//INICIO LISTA MEDIDA CAUTELAR BASE        
    @RequestMapping(value = {"/pad/mant_medida_caut"}, method = RequestMethod.GET)
	public String MantMedidaCaut(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE MEDIDAS CAUTELARES");             
            request.setAttribute("btn_nuevo_reg","pad_mant_medida_caut_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_medida_caut";
	}
//FIN LISTA MEDIDA CAUTELAR BASE
//
//INICIO LISTA MEDIDA CAUTELAR TABLA        
    @RequestMapping(value = {"/pad/mant_medida_caut_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryMedidaCautTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            ConeccionDB cn =  new ConeccionDB();            

            String np = "pad.fn_medida_caut_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String i_id_medida_caut = vss.get(0).toString();
                String c_des_medida_caut = vss.get(1).toString();
                String c_est_reg = vss.get(2).toString();
                 
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_medida_caut_popup(\\\""+i_id_medida_caut+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i_id_medida_caut);
                vv.add(c_des_medida_caut);
                vv.add(c_est_reg);
                vv.add(btn);
                v_temp.add(vv);                
            }     
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");sv.add("true");vc_tbl.add(sv);sv =  new Vector();
            sv.add("sScrollY");sv.add("'93%'");vc_tbl.add(sv);sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'C�DIGO'} , "
                                    + "{'sTitle':'MEDIDA CAUTELAR'} , "
                                    + "{'sTitle':'ESTADO'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");vc_tbl.add(sv);sv =  new Vector();
            sv.add("aaData");sv.add(json);vc_tbl.add(sv);sv =  new Vector();
        //      sv.add("aoColumnDefs");sv.add("[{'sClass':'center','aTargets':[0,1,4,5,6]},{'aTargets':[ 10 ],'bVisible': false,'bSearchable': false}]");vc_tbl.add(sv);sv =  new Vector();
            //boton de excel
            sv.add("dom");sv.add("'Bfrtip'");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm' }]");vc_tbl.add(sv);sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_medida_caut'></table>";
            String tbl = util.datatable("c_tbl_medida_caut",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_medida_caut_tbl";
	}
//FIN LISTA MEDIDA CAUTELAR TABLA
//    
//INICIO MEDIDA CAUTELAR POPUP            
    @RequestMapping(value = {"/pad/mant_medida_caut_popup"}, method = RequestMethod.GET)
    public String MantMedidaCautPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE MEDIDA CAUTELAR");
        String id = request.getParameter("id");
           
        try {            
            ConeccionDB cn = new ConeccionDB(); 
            
            String np = "pad.fn_medida_caut_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String i_id_med = "";
            String c_des_med = "";
            String c_est_reg = "";            
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                i_id_med = datos_v.get(0).toString();
                c_des_med = datos_v.get(1).toString();
                c_est_reg = datos_v.get(3).toString();
            }            
            request.setAttribute("id", i_id_med);  
            request.setAttribute("descripcion", c_des_med);  
            
//          informaci�n para el combo Estado
            String nt = "sgd.fn_estado_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
            String cb_desc_estado = util.contenido_combo(datos_cbo, c_est_reg);
            request.setAttribute("cb_estado", cb_desc_estado);
            
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_medida_caut_popup";
    }   
//FIN MEDIDA CAUTELAR POPUP
// 
//INICIO MEDIDA CAUTELAR GUARDAR    
@RequestMapping(value = {"/pad/mant_medida_caut_guardar"}, method = RequestMethod.GET)
    public String MantMedidaCautGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_medida_caut_mant";
            String array[] = new String[3];
            array[0] = id;
            array[1] = descripcion; 
            array[2] = estado; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_medida_caut_guardar";
    }
//FIN MEDIDA CAUTELAR GUARDAR     
//
//INICIO LISTA FALTAS BASE - LITERAL       
    @RequestMapping(value = {"/pad/mant_literal"}, method = RequestMethod.GET)
	public String MantLiteral(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE FALTAS");             
            request.setAttribute("btn_nuevo_reg","pad_mant_falta_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_literal";
	}
//FIN LISTA FALTAS BASE - LITERAL    
//         
//INICIO LISTA LITERAL TABLA        
    @RequestMapping(value = {"/pad/mant_literal_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryLiteralTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            ConeccionDB cn =  new ConeccionDB();            

            String np = "pad.fn_literal_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String id_literal = vss.get(0).toString();
                String des_literal = vss.get(1).toString();
                String n_articulo = vss.get(2).toString();
                String des_norma = vss.get(3).toString();
                 
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_falta_popup(\\\""+id_literal+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(id_literal);
                vv.add(des_norma);
                vv.add(n_articulo);
                vv.add(des_literal);
                vv.add(btn);
                v_temp.add(vv);                
            }     
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");sv.add("true");vc_tbl.add(sv);sv =  new Vector();
            sv.add("sScrollY");sv.add("'93%'");vc_tbl.add(sv);sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'N�'} , "
                                    + "{'sTitle':'NORMA'} , "
                                    + "{'sTitle':'ART�CULO'} , "
                                    + "{'sTitle':'FALTA'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");vc_tbl.add(sv);sv =  new Vector();
            sv.add("aaData");sv.add(json);vc_tbl.add(sv);sv =  new Vector();
        //      sv.add("aoColumnDefs");sv.add("[{'sClass':'center','aTargets':[0,1,4,5,6]},{'aTargets':[ 10 ],'bVisible': false,'bSearchable': false}]");vc_tbl.add(sv);sv =  new Vector();
            //boton de excel
            sv.add("dom");sv.add("'Bfrtip'");vc_tbl.add(sv);sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm' }]");vc_tbl.add(sv);sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_falta'></table>";
            String tbl = util.datatable("c_tbl_falta",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_literal_tbl";
	}
//FIN LISTA LITERAL TABLA
// 
//INICIO FALTAS POPUP            
    @RequestMapping(value = {"/pad/mant_falta_popup"}, method = RequestMethod.GET)
    public String MantFaltaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE FALTAS");
        String id = request.getParameter("id");
        
        try {
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_literal_popup_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String id_literal = "";
            String des_literal = "";
            String cod_literal = "";
            String c_est_reg = "";
            String id_articulo = "";
            String id_capitulo = "";
            String id_titulo = "";
            String id_norma = "";
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                id_literal = datos_v.get(0).toString();
                des_literal = datos_v.get(1).toString();
                cod_literal = datos_v.get(2).toString();
                c_est_reg = datos_v.get(3).toString();
                id_articulo = datos_v.get(4).toString();
                id_capitulo = datos_v.get(5).toString();
                id_titulo = datos_v.get(6).toString();
                id_norma = datos_v.get(7).toString();
            }
            request.setAttribute("id", id_literal);
            request.setAttribute("descripcion", des_literal);
            request.setAttribute("cod_literal", cod_literal);
            
            String cb_desc_estado = "";
//          informaci�n para el combo Estado
                String nt = "sgd.fn_estado_consulta";
                String array_cbo[] = new String[1];
                array_cbo[0] = "";
                Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
                if (id_literal.length() != 0){
                    cb_desc_estado = util.contenido_combo(datos_cbo, c_est_reg);
                }else{
                    cb_desc_estado = util.contenido_combo(datos_cbo, "1");    
                }  
                request.setAttribute("cb_estado", cb_desc_estado);    
                
            String cb_desc_articulo = "";
//          informaci�n para el combo Art�culo            
                String articulo = "pad.fn_articulo_consulta";
                String array_cbo_articulo[] = new String[1];
                array_cbo_articulo[0] = "";
                Vector datos_cbo_articulo = cn.EjecutarProcedurePostgres(articulo, array_cbo_articulo);
                cb_desc_articulo = util.contenido_combo(datos_cbo_articulo, id_articulo);                
                request.setAttribute("cb_articulo", cb_desc_articulo);                               
          
            String cb_desc_capitulo = "";
//          informaci�n para el combo Cap�tulo            
                String capitulo = "pad.fn_capitulo_consulta";
                String array_cbo_capitulo[] = new String[1];
                array_cbo_capitulo[0] = "";
                Vector datos_cbo_capitulo = cn.EjecutarProcedurePostgres(capitulo, array_cbo_capitulo);
                cb_desc_capitulo = util.contenido_combo(datos_cbo_capitulo, id_capitulo);                
                request.setAttribute("cb_capitulo", cb_desc_capitulo);                               
          
            String cb_desc_titulo = "";
//          informaci�n para el combo Cap�tulo            
                String titulo = "pad.fn_titulo_consulta";
                String array_cbo_titulo[] = new String[1];
                array_cbo_titulo[0] = "";
                Vector datos_cbo_titulo = cn.EjecutarProcedurePostgres(titulo, array_cbo_titulo);
                cb_desc_titulo = util.contenido_combo(datos_cbo_titulo, id_titulo);                
                request.setAttribute("cb_titulo", cb_desc_titulo);                               
          
            String cb_desc_norma = "";
//          informaci�n para el combo Cap�tulo            
                String norma = "pad.fn_norma_consulta";
                String array_cbo_norma[] = new String[1];
                array_cbo_norma[0] = "";
                Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
                cb_desc_norma = util.contenido_combo(datos_cbo_norma, id_norma);                
                request.setAttribute("cb_norma", cb_desc_norma);                               
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_falta_popup";
    }   
//FIN FALTAS POPUP
// 
//INICIO FALTA GUARDAR    
@RequestMapping(value = {"/pad/mant_falta_guardar"}, method = RequestMethod.GET)
    public String MantFaltaGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");   
        String articulo = request.getParameter("articulo");   
        String codigo_lit = request.getParameter("codigo_lit");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_falta_mant";
            String array[] = new String[5];
            array[0] = id;
            array[1] = descripcion; 
            array[2] = estado; 
            array[3] = articulo; 
            array[4] = codigo_lit; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_falta_guardar";
    }
//FIN FALTA GUARDAR    
//        
//INICIO CARCAR COMBO LITERAL POR NORMA
    @RequestMapping(value = {"/pad/mant_literal_norma_consulta"}, method = RequestMethod.GET)
    public String MantLiteralNormaQuery(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException { 
        String var_request = "";
        try {
            String norma_jur = request.getParameter("norma_jur");
            
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_literal_norma_consulta";
            String array[] = new String[1];
            array[0] = norma_jur;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            var_request = new Util().contenido_combo(datos,"");
            
        } catch (Exception ex) {
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("response", var_request);
        
        return "pad/mant_literal_norma_consulta";
    } 
//FIN CARCAR COMBO LITERAL POR NORMA
//              
//INICIO CARCAR COMBO LITERAL POR NORMA
    @RequestMapping(value = {"/pad/mant_articulo_capitulo_consulta"}, method = RequestMethod.GET)
    public String MantArticuloCapituloQuery(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException { 
        String var_request = "";
        try {
            String capitulo = request.getParameter("capitulo");
            
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_articulo_capitulo_consulta";
            String array[] = new String[1];
            array[0] = capitulo;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            var_request = new Util().contenido_combo(datos,"");
            
        } catch (Exception ex) {
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("response", var_request);
        
        return "pad/mant_articulo_capitulo_consulta";
    } 
//FIN CARCAR COMBO LITERAL POR NORMA
//              
//INICIO CARCAR COMBO CAPITULO POR TITULO
    @RequestMapping(value = {"/pad/mant_capitulo_titulo_consulta"}, method = RequestMethod.GET)
    public String MantCapituloTituloQuery(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException { 
        String var_request = "";
        try {
            String titulo = request.getParameter("titulo");
            
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_capitulo_titulo_consulta";
            String array[] = new String[1];
            array[0] = titulo;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            var_request = new Util().contenido_combo(datos,"");
            
        } catch (Exception ex) {
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("response", var_request);
        
        return "pad/mant_capitulo_titulo_consulta";
    } 
//FIN CARCAR COMBO CAPITULO POR TITULO
//              
//INICIO CARCAR COMBO TITULO POR NORMA
    @RequestMapping(value = {"/pad/mant_titulo_norma_consulta"}, method = RequestMethod.GET)
    public String MantTituloNormaQuery(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException { 
        String var_request = "";
        try {
            String norma = request.getParameter("norma");
            
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_titulo_norma_consulta";
            String array[] = new String[1];
            array[0] = norma;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            var_request = new Util().contenido_combo(datos,"");
            
        } catch (Exception ex) {
            Logger.getLogger(SgdController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("response", var_request);
        
        return "pad/mant_titulo_norma_consulta";
    } 
//FIN CARCAR COMBO COMBO TITULO POR NORMA
// 
//INICIO REPORTE NO HA LUGAR        
    @RequestMapping(value = {"/pad/mant_reporte_procedimiento"}, method = RequestMethod.GET)
	public String MantReporteNohalugar(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","REPORTE POR PROCEDIMIENTO");
        
            try {
            ConeccionDB cn = new ConeccionDB();               
            Util util =  new Util();
//          informaci�n para el combo periodo (a�o)
            String ne = "pad.fn_anio_exp_consulta";
            String array_cbo_anio[] = new String[1];
            array_cbo_anio[0] = "";
            Vector datos_cbo_anio = cn.EjecutarProcedurePostgres(ne, array_cbo_anio);            
            String cb_anio = util.contenido_combo(datos_cbo_anio, "");
            request.setAttribute("anio", cb_anio);
            
//            informaci�n combo: tipo de procedimiento
            String cons_tipo_proced = "pad.fn_tipo_proced_consulta";//consulta combo destino                  
            String array_tipo_proced[] = new String[1];
            array_tipo_proced[0] = "";
            Vector datos_cbo_tipo_proced = cn.EjecutarProcedurePostgres(cons_tipo_proced, array_tipo_proced); 
            String cb_datos_tipo_proced = util.contenido_combo(datos_cbo_tipo_proced, "");
            request.setAttribute("tipo_proced", cb_datos_tipo_proced);          
            
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }            
            return "pad/mant_reporte_procedimiento";
	}
//FIN REPORTE NO HA LUGAR
//
//INICIO REPORTE DE EXPEDIENTES POR TABLA        
    @RequestMapping(value = {"/pad/mant_reporte_procedimiento_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryReporteProcedimientoTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
            
            String anio = request.getParameter("anio");    
            String procedimiento = request.getParameter("procedimiento");
            
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_reporte_procedimiento_consulta";
            String array[] = new String[2];
            array[0] = anio;
            array[1] = procedimiento;
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String nro_exp = vss.get(0).toString();
                String fecharecep = vss.get(1).toString();
                String v_etapa = vss.get(2).toString();
                String v_estado = vss.get(4).toString();
                String v_abogado = vss.get(5).toString();
                String fec_presipad = vss.get(7).toString();
                String fec_prespad = vss.get(9).toString();
                String des_proced = vss.get(11).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_expedientes_pad_consulta_popup(\\\""+nro_exp+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(i+1);
                vv.add(nro_exp);
                vv.add(fecharecep);
                vv.add(fec_presipad);
                vv.add(fec_prespad);
                vv.add(v_etapa);
                vv.add(v_estado);
                vv.add(des_proced);
                vv.add(v_abogado);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["                                    
                                    + "{'sTitle':'ITEM'} , "
                                    + "{'sTitle':'N� EXPEDIENTE'} , "
                                    + "{'sTitle':'FECHA RECEP. ORH'} , "
                                    + "{'sTitle':'FECHA PRESCR. INIC.PAD'} , "
                                    + "{'sTitle':'FECHA PRESCR. PAD'} , "
                                    + "{'sTitle':'ETAPA'} , "
                                    + "{'sTitle':'ESTADO'} , "
                                    + "{'sTitle':'PROCEDIMIENTO'} , "
                                    + "{'sTitle':'ABOGADO'} , "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered  text-center' id='c_tbl_reporte_por'></table>";
            String tbl = util.datatable("c_tbl_reporte_por",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_reporte_procedimiento_tbl";
	}
//FIN REPORTE DE EXPEDIENTES POR TABLA
//
//INICIO NORMA BASE        
    @RequestMapping(value = {"/pad/mant_norma"}, method = RequestMethod.GET)
	public String MantNorma(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE NORMAS Y REGLAMENTOS LEGALES");             
            request.setAttribute("btn_nuevo_reg","pad_mant_norma_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_norma";
	}
//FIN NORMA BASE
//
//INICIO NORMA TABLA        
    @RequestMapping(value = {"/pad/mant_norma_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryNormaTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
                        
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_norma_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String id = vss.get(0).toString();
                String descripcion = vss.get(1).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_norma_popup(\\\""+id+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(id);
                vv.add(descripcion);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["
                                    + "{'sTitle':'ID'} , "
                                    + "{'sTitle':'NORMA / REGLAMENTO LEGAL'},  "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_norma'></table>";
            String tbl = util.datatable("c_tbl_norma",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_norma_tbl";
	}
//FIN NORMA TABLA
//
//INICIO NORMA POPUP            
    @RequestMapping(value = {"/pad/mant_norma_popup"}, method = RequestMethod.GET)
    public String MantNormaPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE NORMAS / REGLAMENTOS");
        String id = request.getParameter("id");
           
        try {            
            ConeccionDB cn = new ConeccionDB(); 
            
            String np = "pad.fn_norma_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String id_norma = "";
            String n_norma = "";
            String des_norma = "";
            String est_reg = "";            
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                id_norma = datos_v.get(0).toString();
                des_norma = datos_v.get(3).toString();
                n_norma = datos_v.get(2).toString();
                est_reg = datos_v.get(4).toString();
            }            
            request.setAttribute("id", id_norma);  
            request.setAttribute("n_norma", n_norma);  
            request.setAttribute("descripcion", des_norma);  
            
//          informaci�n para el combo Estado
            String cb_desc_estado = "";
            String nt = "sgd.fn_estado_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
            if (id_norma.length() != 0){
                    cb_desc_estado = util.contenido_combo(datos_cbo, est_reg);
                }else{
                    cb_desc_estado = util.contenido_combo(datos_cbo, "1");    
                }  
                request.setAttribute("cb_estado", cb_desc_estado);            
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_norma_popup";
    }   
//FIN NORMA POPUP
//
//INICIO NORMA GUARDAR    
@RequestMapping(value = {"/pad/mant_norma_guardar"}, method = RequestMethod.GET)
    public String MantNormaGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String nro = request.getParameter("nro");   
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_norma_mant";
            String array[] = new String[4];
            array[0] = id;
            array[1] = nro; 
            array[2] = descripcion; 
            array[3] = estado; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_norma_guardar";
    }
//FIN NORMA GUARDAR     
//
//INICIO TITULO BASE        
    @RequestMapping(value = {"/pad/mant_titulo"}, method = RequestMethod.GET)
	public String MantTitulo(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE TITULOS");             
            request.setAttribute("btn_nuevo_reg","pad_mant_titulo_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_titulo";
	}
//FIN NORMA BASE
// 
//INICIO TITULO TABLA        
    @RequestMapping(value = {"/pad/mant_titulo_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryTituloTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
                        
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_titulo_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String id = vss.get(0).toString();
                String descripcion = vss.get(1).toString();
                String norma = vss.get(6).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_titulo_popup(\\\""+id+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(id);
                vv.add(norma);
                vv.add(descripcion);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["
                                    + "{'sTitle':'ID'} , "
                                    + "{'sTitle':'NORMA / REGLAMENTO LEGAL'} , "
                                    + "{'sTitle':'TITULO'},  "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_titulo'></table>";
            String tbl = util.datatable("c_tbl_titulo",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_titulo_tbl";
	}
//FIN TITULO TABLA
//
//INICIO TITULO POPUP            
    @RequestMapping(value = {"/pad/mant_titulo_popup"}, method = RequestMethod.GET)
    public String MantTituloPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE T�TULOS");
        String id = request.getParameter("id");
           
        try {            
            ConeccionDB cn = new ConeccionDB(); 
            
            String np = "pad.fn_titulo_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String id_titulo = "";
            String n_titulo = "";
            String des_titulo = "";
            String est_reg = "";            
            String id_norma = "";            
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                id_titulo = datos_v.get(0).toString();
                n_titulo = datos_v.get(2).toString();
                des_titulo = datos_v.get(3).toString();
                est_reg = datos_v.get(4).toString();
                id_norma = datos_v.get(5).toString();
            }            
            request.setAttribute("id", id_titulo);  
            request.setAttribute("n_titulo", n_titulo);  
            request.setAttribute("descripcion", des_titulo);  
            
//          informaci�n para el combo Estado
            String cb_desc_estado = "";
            String nt = "sgd.fn_estado_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
            if (id_titulo.length() != 0){
                    cb_desc_estado = util.contenido_combo(datos_cbo, est_reg);
            }else{
                cb_desc_estado = util.contenido_combo(datos_cbo, "1");    
            }  
            request.setAttribute("cb_estado", cb_desc_estado);       
            
            String cb_desc_norma = "";
//          informaci�n para el combo norma            
                String norma = "pad.fn_norma_consulta";
                String array_cbo_norma[] = new String[1];
                array_cbo_norma[0] = "";
                Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
                cb_desc_norma = util.contenido_combo(datos_cbo_norma, id_norma);                
                request.setAttribute("cb_norma", cb_desc_norma);            
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_titulo_popup";
    }   
//FIN TITULO POPUP
//
//INICIO NORMA GUARDAR    
@RequestMapping(value = {"/pad/mant_titulo_guardar"}, method = RequestMethod.GET)
    public String MantTituloGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String nro = request.getParameter("nro");   
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");   
        String id_norma = request.getParameter("id_norma");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_titulo_mant";
            String array[] = new String[5];
            array[0] = id;
            array[1] = nro; 
            array[2] = descripcion; 
            array[3] = estado; 
            array[4] = id_norma; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_titulo_guardar";
    }
//FIN NORMA GUARDAR     
//
//INICIO TITULO BASE        
    @RequestMapping(value = {"/pad/mant_capitulo"}, method = RequestMethod.GET)
	public String MantCapitulo(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE CAPITULOS");             
            request.setAttribute("btn_nuevo_reg","pad_mant_capitulo_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_capitulo";
	}
//FIN TITULO BASE
// 
//INICIO CAPITULO TABLA        
    @RequestMapping(value = {"/pad/mant_capitulo_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryCapituloTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
                        
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_capitulo_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String id = vss.get(0).toString();
                String descripcion = vss.get(1).toString();
                String norma = vss.get(8).toString();
                String titulo = vss.get(6).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_capitulo_popup(\\\""+id+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(id);
                vv.add(norma);
                vv.add(titulo);
                vv.add(descripcion);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["
                                    + "{'sTitle':'ID'} , "
                                    + "{'sTitle':'NORMA / REGLAMENTO LEGAL'} , "
                                    + "{'sTitle':'TITULO'},  "
                                    + "{'sTitle':'CAPITULO'},  "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_titulo'></table>";
            String tbl = util.datatable("c_tbl_titulo",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_capitulo_tbl";
	}
//FIN CAPITULO TABLA
//
//INICIO CAPITULO POPUP            
    @RequestMapping(value = {"/pad/mant_capitulo_popup"}, method = RequestMethod.GET)
    public String MantCapituloPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE CAP�TULOS");
        String id = request.getParameter("id");
           
        try {            
            ConeccionDB cn = new ConeccionDB(); 
            
            String np = "pad.fn_capitulo_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String id_capitulo = "";
            String n_capitulo = "";
            String des_capitulo = "";
            String est_reg = "";            
            String id_norma = "";
            String id_titulo = "";            
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                id_capitulo = datos_v.get(0).toString();
                n_capitulo = datos_v.get(2).toString();
                des_capitulo = datos_v.get(3).toString();
                est_reg = datos_v.get(4).toString();
                id_norma = datos_v.get(7).toString();
                id_titulo = datos_v.get(5).toString();
            }            
            request.setAttribute("id", id_capitulo);  
            request.setAttribute("n_capitulo", n_capitulo);  
            request.setAttribute("descripcion", des_capitulo);  
            
//          informaci�n para el combo Estado
            String cb_desc_estado = "";
            String nt = "sgd.fn_estado_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
            if (id_titulo.length() != 0){
                    cb_desc_estado = util.contenido_combo(datos_cbo, est_reg);
            }else{
                cb_desc_estado = util.contenido_combo(datos_cbo, "1");    
            }  
            request.setAttribute("cb_estado", cb_desc_estado);       
            
            String cb_desc_norma = "";
//          informaci�n para el combo norma            
                String norma = "pad.fn_norma_consulta";
                String array_cbo_norma[] = new String[1];
                array_cbo_norma[0] = "";
                Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
                cb_desc_norma = util.contenido_combo(datos_cbo_norma, id_norma);                
                request.setAttribute("cb_norma", cb_desc_norma);  
                
            String cb_desc_titulo = "";
//          informaci�n para el combo t�tulo            
                String titulo = "pad.fn_titulo_consulta";
                String array_cbo_titulo[] = new String[1];
                array_cbo_titulo[0] = "";
                Vector datos_cbo_titulo = cn.EjecutarProcedurePostgres(titulo, array_cbo_titulo);
                cb_desc_titulo = util.contenido_combo(datos_cbo_titulo, id_titulo);                
                request.setAttribute("cb_titulo", cb_desc_titulo);      
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_capitulo_popup";
    }   
//FIN CAPITULO POPUP
//
//INICIO CAPITULO GUARDAR    
@RequestMapping(value = {"/pad/mant_capitulo_guardar"}, method = RequestMethod.GET)
    public String MantCapituloGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String nro = request.getParameter("nro");   
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");    
        String id_titulo = request.getParameter("id_titulo");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_capitulo_mant";
            String array[] = new String[5];
            array[0] = id;
            array[1] = nro; 
            array[2] = descripcion; 
            array[3] = estado;  
            array[4] = id_titulo; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_capitulo_guardar";
    }
//FIN CAPITULO GUARDAR     
//
//INICIO ARTICULO BASE        
    @RequestMapping(value = {"/pad/mant_articulo"}, method = RequestMethod.GET)
	public String MantArticulo(HttpServletRequest request, HttpServletResponse response,ModelMap model) {            
            request.setAttribute("title_pag","GESTI�N DE ART�CULOS");             
            request.setAttribute("btn_nuevo_reg","pad_mant_articulo_popup()");
            request.setAttribute("tit_btn","NUEVO REGISTRO");
            return "pad/mant_articulo";
	}
//FIN ARTICULO BASE
// 
//INICIO ARTICULO TABLA        
    @RequestMapping(value = {"/pad/mant_articulo_tbl"}, method = RequestMethod.GET)
	public String AjaxQueryArticuloTbl(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
                        
            ConeccionDB cn =  new ConeccionDB();  
            String np = "pad.fn_articulo_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector v_datos = cn.EjecutarProcedurePostgres(np, array);

            Vector v_temp = new Vector();
            for(int i = 0 ; i<v_datos.size() ; i++){
                Vector vss =  (Vector) v_datos.get(i);
                String id = vss.get(0).toString();
                String descripcion = vss.get(1).toString();
                String norma = vss.get(10).toString();
                String titulo = vss.get(8).toString();
                String capitulo = vss.get(6).toString();
                
                String btn = "<button type='button' class='btn btn-info' onclick='pad_mant_articulo_popup(\\\""+id+"\\\")'><span class='glyphicon glyphicon-edit'></span></button>";
                
                Vector vv = new Vector();
                vv.add(id);
                vv.add(norma);
                vv.add(titulo);
                vv.add(capitulo);
                vv.add(descripcion);
                vv.add(btn);
                v_temp.add(vv);
            }
            
            Util util = new Util();
            String json = util.vector2json(v_temp);   
            Vector vc_tbl = new Vector();
            Vector sv =  new Vector();
            sv.add("bScrollCollapse");
            sv.add("true");vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("sScrollY");
            sv.add("'93%'");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aoColumns");sv.add("["
                                    + "{'sTitle':'ID'} , "
                                    + "{'sTitle':'NORMA / REGLAMENTO LEGAL'} , "
                                    + "{'sTitle':'T�TULO'},  "
                                    + "{'sTitle':'CAP�TULO'},  "
                                    + "{'sTitle':'ART�CULO'},  "
                                    + "{'sTitle':'-'}  "
                                    + "]");
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("aaData");
            sv.add(json);
            vc_tbl.add(sv);
            sv =  new Vector();
            sv.add("dom");
            sv.add("'Bfrtip'");
            vc_tbl.add(sv);
            sv =  new Vector();
//            sv.add("buttons");sv.add("['excel']");vc_tbl.add(sv);sv =  new Vector();
            sv.add("buttons");sv.add("[{ extend:'excel',text:'Exportar a Excel',className:'btn btn-info btn-sm',exportOptions:{columns:[1,2,3,4,5,6]} },"
//                                    + "{ extend:'pdf',text:'Exportar a PDF',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',exportOptions:{columns:[1,2,3,4,5,6]},orientation:'landscape',pageSize:'A4',download:'open' },"
//                                    + "{ extend:'print',text:'imprimir',className:'btn btn-info btn-sm',title:'Secretar�a T�cnica del Procedimiento Administrativo Disciplinario - PAD',messageTop:'REPORTE DE EXPEDIENTES',exportOptions:{columns:[1,2,3,4,5,6]} }"
                                    + " ]");
            vc_tbl.add(sv);
            sv =  new Vector();
            ////Pintar de rojo el registro si no t.iene datos
//            String fnc = "function( nRow, aData, iDisplayIndex ){ "+
//                            " if (rtrim(aData[2]) == 'CONFIDENCIAL'){$('td', nRow).addClass('ui-state-error' );} " +                     
//                          "}";
//            sv.add("fnRowCallback");sv.add(fnc);vc_tbl.add(sv);sv =  new Vector();

            String tbl_html = "<table border='1' class='table table-striped table-bordered' id='c_tbl_articulo'></table>";
            String tbl = util.datatable("c_tbl_articulo",vc_tbl);            
            request.setAttribute("response", tbl_html + tbl);

            return "pad/mant_articulo_tbl";
	}
//FIN ARTICULO TABLA
//
//
//INICIO ARTICULO POPUP            
    @RequestMapping(value = {"/pad/mant_articulo_popup"}, method = RequestMethod.GET)
    public String MantArticuloPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","GESTI�N DE ART�CULOS");
        String id = request.getParameter("id");
           
        try {            
            ConeccionDB cn = new ConeccionDB(); 
            
            String np = "pad.fn_articulo_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            
            Util util =  new Util();
            
            String id_articulo = "";
            String n_articulo = "";
            String des_articulo = "";
            String est_reg = "";            
            String id_norma = "";
            String id_titulo = "";            
            String id_capitulo = "";            
            
            for(int i = 0 ; i<datos.size() ; i++){
                Vector datos_v =  (Vector) datos.get(i);
                id_articulo = datos_v.get(0).toString();
                n_articulo = datos_v.get(2).toString();
                des_articulo = datos_v.get(3).toString();
                est_reg = datos_v.get(4).toString();
                id_norma = datos_v.get(9).toString();
                id_titulo = datos_v.get(7).toString();
                id_capitulo = datos_v.get(5).toString();
            }            
            request.setAttribute("id", id_articulo);  
            request.setAttribute("n_articulo", n_articulo);  
            request.setAttribute("descripcion", des_articulo);  
            
//          informaci�n para el combo Estado
            String cb_desc_estado = "";
            String nt = "sgd.fn_estado_consulta";
            String array_cbo[] = new String[1];
            array_cbo[0] = "";
            Vector datos_cbo = cn.EjecutarProcedurePostgres(nt, array_cbo);
            if (id_titulo.length() != 0){
                    cb_desc_estado = util.contenido_combo(datos_cbo, est_reg);
            }else{
                cb_desc_estado = util.contenido_combo(datos_cbo, "1");    
            }  
            request.setAttribute("cb_estado", cb_desc_estado);       
            
            String cb_desc_norma = "";
//          informaci�n para el combo norma            
                String norma = "pad.fn_norma_consulta";
                String array_cbo_norma[] = new String[1];
                array_cbo_norma[0] = "";
                Vector datos_cbo_norma = cn.EjecutarProcedurePostgres(norma, array_cbo_norma);
                cb_desc_norma = util.contenido_combo(datos_cbo_norma, id_norma);                
                request.setAttribute("cb_norma", cb_desc_norma);  
                
            String cb_desc_titulo = "";
//          informaci�n para el combo t�tulo            
                String titulo = "pad.fn_titulo_consulta";
                String array_cbo_titulo[] = new String[1];
                array_cbo_titulo[0] = "";
                Vector datos_cbo_titulo = cn.EjecutarProcedurePostgres(titulo, array_cbo_titulo);
                cb_desc_titulo = util.contenido_combo(datos_cbo_titulo, id_titulo);                
                request.setAttribute("cb_titulo", cb_desc_titulo);      
                
            String cb_desc_capitulo = "";
//          informaci�n para el combo Cap�tulo            
                String capitulo = "pad.fn_capitulo_consulta";
                String array_cbo_capitulo[] = new String[1];
                array_cbo_capitulo[0] = "";
                Vector datos_cbo_capitulo = cn.EjecutarProcedurePostgres(capitulo, array_cbo_capitulo);
                cb_desc_capitulo = util.contenido_combo(datos_cbo_capitulo, id_capitulo);                
                request.setAttribute("cb_capitulo", cb_desc_capitulo);                         
          
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_articulo_popup";
    }   
//FIN ARTICULO POPUP
//
//INICIO ARTICULO GUARDAR    
@RequestMapping(value = {"/pad/mant_articulo_guardar"}, method = RequestMethod.GET)
    public String MantArticuloGuardar(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {        

        String id = request.getParameter("id");        
        String nro = request.getParameter("nro");   
        String descripcion = request.getParameter("descripcion");   
        String estado = request.getParameter("estado");    
        String id_capitulo = request.getParameter("id_capitulo");   
        String var_request = "";

        try {
            ConeccionDB cdb = new ConeccionDB();                
            String np = "pad.fn_articulo_mant";
            String array[] = new String[5];
            array[0] = id;
            array[1] = nro; 
            array[2] = descripcion; 
            array[3] = estado;  
            array[4] = id_capitulo; 
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_articulo_guardar";
    }
//FIN ARTICULO GUARDAR     
//
//INICIO ALERTA POPUP
    @RequestMapping(value = {"/pad/mant_alertaip_popup"}, method = RequestMethod.GET)
    public String MantAlertaipPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model)
            throws ServletException, IOException {
        request.setAttribute("title_pag","ALERTAS");
        String id = "";
        
        try {
            ConeccionDB cn = new ConeccionDB();
            
            String np = "pad.fn_alertaip_consulta";
            String array[] = new String[1];
            array[0] = id;
            Vector datos = cn.EjecutarProcedurePostgres(np, array);
            Integer ii = 0;            
            String n_exp = "";
            String dias = "";
            String fec_ipad = "";
            String tbl_exp = "<p>Inicio de PAD</p><table id='tbl_ipad' class='table table-striped'>" +
                             "<tr class='success'>" +
                             "  <td>ITEM</td>" +
                             "  <td>N� EXPEDIENTE</td>" +
                             "  <td>D�AS A CADUCAR</td>" +
                             "  <td>FECHA IPAD</td>" +
                             "</tr>";
            
            for(int i = 0; i<datos.size(); i++){
                Vector datos_v = (Vector) datos.get(i);
                n_exp = datos_v.get(0).toString();
                fec_ipad = datos_v.get(1).toString();
                dias = datos_v.get(2).toString();
                ii = i+1;                
                tbl_exp += "<tr>" +
                    "  <td class='text-center'>"+ii+"</td>" +
                    "  <td>"+n_exp+"</td>" +
                    "  <td>"+dias+"</td>" +
                    "  <td>"+fec_ipad+ "</td>" +
                    "</tr>";                                
            }
            tbl_exp += "</table><br>";
            if (ii > 0){
                request.setAttribute("tbl_exp", tbl_exp);
            }else{
                request.setAttribute("tbl_exp", "");
            }
            
            String npad = "pad.fn_alertap_consulta";
            String array_pad[] = new String[1];
            array_pad[0] = id;
            Vector datos_pad = cn.EjecutarProcedurePostgres(npad, array_pad);
            Integer x = 0;            
            String n_exp_p = "";
            String dias_p = "";
            String fec_pad = "";
            String tbl_exp_p = "<p>PAD</p><table id='tbl_pad' class='table table-striped'>" +
                             "<tr class='success'>" +
                             "  <td>ITEM</td>" +
                             "  <td>N� EXPEDIENTE</td>" +
                             "  <td>D�AS A CADUCAR</td>" +
                             "  <td>FECHA PAD</td>" +
                             "</tr>";
            
            for(int i = 0; i<datos_pad.size(); i++){
                Vector datos_v = (Vector) datos_pad.get(i);
                n_exp_p = datos_v.get(0).toString();
                fec_pad = datos_v.get(1).toString();
                dias_p = datos_v.get(2).toString();
                x = i+1;                
                tbl_exp_p += "<tr>" +
                    "  <td class='text-center'>"+x+"</td>" +
                    "  <td>"+n_exp_p+"</td>" +
                    "  <td>"+dias_p+"</td>" +
                    "  <td>"+fec_pad+ "</td>" +
                    "</tr>";                                
            }
            tbl_exp_p += "</table>";
            if (x > 0){
                request.setAttribute("tbl_exp_p", tbl_exp_p);
            }else{
                request.setAttribute("tbl_exp_p", "");
            }
        
        } catch (Exception ex) {
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "pad/mant_alertaip_popup";
    }   
//FIN ALERTA POPUP
//
//INICIO ALERTA CONSULTA
@RequestMapping(value = {"/pad/mant_ipad_consulta"}, method = RequestMethod.GET)
    public String MantIpadConsulta(HttpServletRequest request, HttpServletResponse response, ModelMap model)
        throws ServletException, IOException {
        String var_request = "";
        
        try {
            ConeccionDB cdb = new ConeccionDB();
            String np = "pad.fn_alertaipp_consulta";
            String array[] = new String[1];
            array[0] = "";
            Vector datos = cdb.EjecutarProcedurePostgres(np, array);
            var_request = new Util().vector2json(datos);
            
        } catch (Exception ex) {
            var_request = ex.getMessage();
            Logger.getLogger(PadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("request", var_request);
        return "pad/mant_ipad_consulta";
    }
//FIN ALERTA CONSULTA
//    
}

