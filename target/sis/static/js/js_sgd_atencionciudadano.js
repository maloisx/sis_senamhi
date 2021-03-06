//ATENCI�N AL CIUDADANO

//INICIO BUSCAR EXPEDIENTE ATENCI�N AL CIUDADANO    
function sgd_mant_atencion_ciudadano_tbl(){
    var exp = $('#txt_expediente').val();
    var anio = $('#cb_anio').val();
    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_atencion_ciudadano_tbl/",
            data:     "exp="+exp+
                      "&anio="+anio,
            beforeSend: function(data){
                $('#div_atencion_ciudadano_tbl').html("Cargando...");
            },
            success: function(requestData){
                $('#div_atencion_ciudadano_tbl').html(requestData);
            },
            error: function(requestData, strError, strTipoError){
                $('#div_atencion_ciudadano_tbl').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN BUSCAR EXPEDIENTE ATENCI�N AL CIUDADANO
//
//INICIO BUSCAR PRIMER DOCUMENTO
function sgd_mant_atencion_ciudadano_doc(){
    var exp = $('#txt_expediente').val();
    var anio = $('#cb_anio').val();
    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_atencion_ciudadano_doc/",
            data:     "exp="+exp+
                      "&anio="+anio,
            beforeSend: function(data){
//                $('#div_asunto').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);	
                var asunto  = arrayobj[0][0];
                $('#txt_asunto').val(asunto);                
            },
            error: function(requestData, strError, strTipoError){
//                $('#div_asunto').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN BUSCAR PRIMER DOCUMENTO
//
//INICIO BUSCAR DOCUMENTO
function sgd_mant_busca_ciudadano_dni(tipo_doc){
    var n_doc = $('#txt_dni').val();
    var t_doc = tipo_doc;
    var msj_error = "";
    if(n_doc == ''){
        msj_error = "N� de documento";
    }
    if(msj_error == ''){
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_busca_ciudadano_dni/",
            data:     "n_doc="+n_doc+
                      "&t_doc="+t_doc,
            beforeSend: function(data){
//                $('#div_asunto').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                               
                var id  = "";
                var ndocumento  = "";
                var nombres  = "";
                var direccion  = "";
                var email  = "";
                var telefono  = "";
                $('#div_per_natural').show();
                if (arrayobj.length !== 0){
                    id  = arrayobj[0][0];
                    ndocumento  = arrayobj[0][1];
                    nombres  = arrayobj[0][2];
                    direccion  = arrayobj[0][4];
                    email  = arrayobj[0][5];
                    telefono  = arrayobj[0][6];

                    $('#hd_id').val(id);
                    $('#txt_nombres').val(nombres);
                    $('#lb_nombres').addClass('active');
                    $('#txt_direccion').val(direccion);
                    $('#lb_direccion').addClass('active');
                    $('#txt_email').val(email);
                    $('#lb_email').addClass('active');
                    $('#txt_telefono').val(telefono);
                    $('#lb_telefono').addClass('active');
                    $('#btn_guarda_ciudadano_dni').text('Actualizar');
                    $('#div_msg_registro').hide();
                    
                    if(nombres !== '' && direccion !== '' && email !== '' && telefono !== ''){
                        sgd_mant_tupa_mostrar();
                    }
                }else if (arrayobj.length == 0){
                    $('#div_msg_registro').text('Registre sus datos.');
                    $('#div_msg_registro').show();
                    $('#div_guarda_ciudadano').show();
                    $('#txt_nombres').val(nombres);
                    $('#txt_direccion').val(direccion);
                    $('#txt_email').val(email);
                    $('#txt_telefono').val(telefono);
                }
            },
            error: function(requestData, strError, strTipoError){
                $('#div_msg_registro').show();
//                $('#div_msg_registro').html("Error " + strTipoError +": " + strError);            
            }
        });
        }else{
         $.alert('<h6>Ingrese: ' + msj_error + '</h6>');
    }   
}
//FIN BUSCAR DOCUMENTO
//
//INICIO BUSCAR RUC
function sgd_mant_busca_ciudadano_ruc(tipo_doc){
    var n_doc = $('#txt_ruc').val();
    var t_doc = tipo_doc;
    var msj_error = "";
    if(n_doc == ''){
        msj_error = "N� de RUC";
    }
    if(msj_error == ''){
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_busca_ciudadano_dni/",
            data:     "n_doc="+n_doc+
                      "&t_doc="+t_doc,
            beforeSend: function(data){
//                $('#div_asunto').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                               
                var id  = "";
                var ndocumento  = "";
                var rsocial = "";
                var direccion  = "";
                var email  = "";
                var telefono  = "";
                var sector  = "";
                var representante  = "";
                var dni_representante  = "";
                var telef_rep  = "";
                var email_rep  = "";
                $('#div_per_juridica').show();
                                
                if (arrayobj.length !== 0){
                    id  = arrayobj[0][0];
                    ndocumento  = arrayobj[0][1];
                    rsocial  = arrayobj[0][2];
                    direccion  = arrayobj[0][4];
                    email  = arrayobj[0][5];
                    telefono  = arrayobj[0][6];
                    dni_representante  = arrayobj[0][8];
                    sector  = arrayobj[0][9];
                    representante  = arrayobj[0][3];
                    telef_rep  = arrayobj[0][10];
                    email_rep  = arrayobj[0][11];

                    $('#hd_id').val(id);
                    $('#txt_rsocial').val(rsocial);
                    $('#lb_rsocial').addClass('active');
                    $('#cb_sector').val(sector);
                    $('#cb_sector').change();
                    $('#txt_telefono_ruc').val(telefono);
                    $('#lb_telefono_ruc').addClass('active');
                    $('#txt_email_ruc').val(email);
                    $('#lb_email_ruc').addClass('active');                    
                    $('#txt_direccion_ruc').val(direccion);
                    $('#lbl_direccion_ruc').addClass('active');
                    $('#txt_representante').val(representante);
                    $('#lb_representante').addClass('active');
                    $('#txt_dni_rep').val(dni_representante);
                    $('#lb_dni_rep').addClass('active');
                    $('#txt_telef_rep').val(telef_rep);
                    $('#lb_telef_rep').addClass('active');
                    $('#txt_email_rep').val(email_rep);
                    $('#lb_email_rep').addClass('active');
                    $('#btn_guarda_ciudadano_ruc').text('Actualizar');                    
                    $('#div_msg_registro').hide();
                    
                    if(rsocial !== '' && direccion !== '' && email !== '' && telefono !== '' && sector !== '' && representante !== ''  && telef_rep !== '' && email_rep !== ''){
                        sgd_mant_tupa_mostrar();
                    }
//                    $('#div_cbo_servicio').show();
                }else if (arrayobj.length == 0){
                    $('#div_msg_registro').text('Registre sus datos.');
                    $('#div_msg_registro').show();
                    $('#div_guarda_ciudadano').show();
                    $('#txt_rsocial').val(rsocial);
                    $('#txt_direccion_ruc').val(direccion);
                    $('#cb_sector').val(sector);
                    $('#cb_sector').change();
                    $('#txt_telefono_ruc').val(email);
                    $('#txt_email_ruc').val(telefono);
                    $('#txt_representante').val(representante);
                    $('#txt_telef_rep').val(telef_rep);
                    $('#txt_email_rep').val(email_rep);
                }                
            },
            error: function(requestData, strError, strTipoError){
                $('#div_msg_registro').show();
//                $('#div_msg_registro').html("Error " + strTipoError +": " + strError);            
            }
        });
        }else{
         $.alert('<h6>Ingrese: ' + msj_error + '</h6>');
    }   
}
//FIN BUSCAR RUC
//
//INICIO MOSTRAR TUPA
function sgd_mant_tupa_mostrar(){
    $('#div_infosol').show();
    sgd_tipo_entrega_chkb();
    sgd_rpta_email_chkb();    
}
//FIN MOSTRAR TUPA
//
//INICIO GUARDAR CIUDADANO DNI
function sgd_mant_ciudadano_dni_guardar(){
    var id = $('#hd_id').val();
    var dni = $('#txt_dni').val();
    var nombres = $('#txt_nombres').val();
    var direccion = $('#txt_direccion').val();
    var email = $('#txt_email').val();
    var telefono = $('#txt_telefono').val();
    var dpto_nat = $('#cb_dpto_dir').val();
    var prov_nat = $('#cb_prov_dir').val();
    var distr_nat = $('#cb_dist_dir').val();
    var dpto_nat_desc = $('select[name=cb_dpto_dir] option:selected').text();
    var prov_nat_desc = $('select[name=cb_prov_dir] option:selected').text();
    var distr_nat_desc = $('select[name=cb_dist_dir] option:selected').text();
        
    var msj_error = "";
    if (nombres == ''){
        msj_error += " Nombres." + "<br>";
    }
    if (direccion == ''){
        msj_error += " Direcci�n." + "<br>";
    }
    if (email == '' || !IsMail(email)){
        msj_error += " E-mail." + "<br>";
    }
    if (telefono == ''){
        msj_error += " Tel�fono.";
    }   
    
    if(msj_error == ''){
        $.ajax({
                dataType: "html",
                type:     "GET",
                url:      path + "sgd/mant_ciudadano_dni_guardar/",
                data:     "id="+id
                          +"&dni="+dni
                          +"&nombres="+nombres
                          +"&direccion="+direccion
                          +"&email="+email
                          +"&telefono="+telefono
                          +"&dpto_nat="+dpto_nat
                          +"&prov_nat="+prov_nat
                          +"&distr_nat="+distr_nat
                          +"&dpto_nat_desc="+dpto_nat_desc
                          +"&prov_nat_desc="+prov_nat_desc
                          +"&distr_nat_desc="+distr_nat_desc
                          +"&dpto_nat_desc="+dpto_nat_desc
                          +"&prov_nat_desc="+prov_nat_desc
                          +"&distr_nat_desc="+distr_nat_desc,
                beforeSend: function(data){
                    $('#div_msg_registro').html("Cargando...");
                },
                success: function(requestData){
                    arrayobj = jQuery.parseJSON(requestData);
                    var id  = arrayobj[0][0];
                    var msj = arrayobj[0][1];//datos guardados

                    $('#hd_id').val(id);
                    $('#div_msg_registro').show();
                    $('#div_msg_registro').html(msj);
                    sgd_mant_tupa_mostrar();                  
                    
                },
                error: function(requestData, strError, strTipoError){								
                    $('#div_msg_registro').html("Error " + strTipoError +": " + strError);
                }
            });
        }else{
//            $.alert('<h6>' + msj_error + '</h6>');
            $.confirm({
                title: 'ERROR!',
                content: '<h6>Ingrese: <br>' + msj_error + '</h6>',
                type: 'red',
                typeAnimated: true,
                buttons: {
                    cerrar: {
                        text: 'Cerrar',
                        btnClass: 'btn-red',
                        action: function(){
                        }
                    }
                }
            });
    }
}
//FIN GUARDAR CIUDADANO DNI//
//
//INICIO BUSCAR MAPA
function sgd_mant_mapa_mostrar(){
    var cad = $('#cb_estacion').val();
    var cod_estacion = "";
    var cod_dpto = "";
    
    if (cad.indexOf("_") == -1){
        cod_estacion = cad;
        cod_dpto = $('#cb_dpto').val();
    }else{
        var cadena = cad.split('_');
        cod_estacion = cadena[0];
        cod_dpto = cadena[1];
    }
        
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_mapa_mostrar/",
            data:     "cod_estacion="+cod_estacion
                     +"&cod_dpto="+cod_dpto,
            beforeSend: function(data){
                $('#div_map').html("Cargando...");
            },
            success: function(requestData){
                $('#div_map').html(requestData);
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_map').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN BUSCAR MAPA
//
//INICIO GUARDAR CIUDADANO RUC
function sgd_mant_ciudadano_ruc_guardar(){
    var id = $('#hd_id').val();
    var ruc = $('#txt_ruc').val();
    var rsocial = $('#txt_rsocial').val();
    var direccion_ruc = $('#txt_direccion_ruc').val();
    var sector = $('#cb_sector').val();
    var email = $('#txt_email_ruc').val();
    var telefono = $('#txt_telefono_ruc').val();
    var representante = $('#txt_representante').val();
    var dni_rep = $('#txt_dni_rep').val();    
    var telef_rep = $('#txt_telef_rep').val();    
    var email_rep = $('#txt_email_rep').val();    
    var dpto_dir = $('#cb_dpto_dir_ruc').val();    
    var prov_dir = $('#cb_prov_dir_ruc').val();    
    var dist_dir = $('#cb_dist_dir_ruc').val();    
    var dpto_dir_des = $('select[name=cb_dpto_dir_ruc] option:selected').text();
    var prov_dir_des = $('select[name=cb_prov_dir_ruc] option:selected').text();
    var dist_dir_des = $('select[name=cb_dist_dir_ruc] option:selected').text();
    
    var msj_error = "";
    if (rsocial == ''){
        msj_error += " Raz�n social." + "<br>";
    }
    if (direccion_ruc == ''){
        msj_error += "  Direcci�n." + "<br>";
    }
    if ($('#cb_sector').val().length == 0){
        msj_error += " sector." + "<br>";
    }
    if (email == '' || !IsMail(email)){
        msj_error += " E-mail." + "<br>";
    }
    if (telefono == ''){
        msj_error += " Tel�fono." + "<br>";
    }
    if (representante == ''){
        msj_error += " Contacto." + "<br>";
    }
    if (dni_rep == ''){
        msj_error += " DNI de contacto." + "<br>";
    }
    if (telef_rep == ''){
        msj_error += " N� tel�fono de contacto." + "<br>";
    }
    if (email_rep == '' || !IsMail(email_rep)){
        msj_error += " E-mail de contacto.";
    }
        
//    if (!IsMail(email_rep)){
//        msj_error += " E-mail de contacto." + "\n";
//    }
    
    if(msj_error == ''){
        $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_ciudadano_ruc_guardar/",
            data:     "id="+id
                      +"&ruc="+ruc
                      +"&rsocial="+rsocial
                      +"&direccion_ruc="+direccion_ruc
                      +"&sector="+sector
                      +"&email="+email
                      +"&telefono="+telefono
                      +"&representante="+representante
                      +"&dni_rep="+dni_rep
                      +"&telef_rep="+telef_rep
                      +"&email_rep="+email_rep
                      +"&dpto_dir="+dpto_dir
                      +"&prov_dir="+prov_dir
                      +"&dist_dir="+dist_dir
                      +"&dpto_dir_des="+dpto_dir_des
                      +"&prov_dir_des="+prov_dir_des
                      +"&dist_dir_des="+dist_dir_des,
            beforeSend: function(data){
                $('#div_msg_registro').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                var id  = arrayobj[0][0];
                var msj = arrayobj[0][1];//datos guardados
                
                $('#hd_id').val(id);
                $('#div_msg_registro').show();
                $('#div_msg_registro').html(msj);
                sgd_mant_tupa_mostrar();
            },
            error: function(requestData, strError, strTipoError){
                $('#div_msg_registro').html("Error " + strTipoError +": " + strError);
            }
        });
    }else{
         //$.alert('<h6>Ingrese: ' + msj_error + '</h6>');
         $.confirm({
                title: 'ERROR!',
                content: '<h6>Ingrese: <br>' + msj_error + '</h6>',
                type: 'red',
                typeAnimated: true,
                buttons: {
                    cerrar: {
                        text: 'Cerrar',
                        btnClass: 'btn-red',
                        action: function(){
                        }
                    }
                }
            });
    }
}
//FIN GUARDAR CIUDADANO RUC

///////////////////////////////////////
var map, manager;
var centerLatitude = -9.49282, centerLongitude = -75.3689, startZoom = 6;
var markersArray = [];
var arrayestaciones;
var popup = new google.maps.InfoWindow();

var optionsmaps = {
    zoom: startZoom
    , center: new google.maps.LatLng(centerLatitude, centerLongitude)
//        , mapTypeId: google.maps.MapTypeId.SATELLITE
    , mapTypeId: google.maps.MapTypeId.HYBRID

    , backgroundColor: '#ffffff'
    , noClear: true
    , disableDefaultUI: true
    , keyboardShortcuts: false
    , disableDoubleClickZoom: true
    , draggable: true
//        , scrollwheel: true
    , draggableCursor: 'move'
    , draggingCursor: 'move'

    , mapTypeControl: true
    , mapTypeControlOptions: {
        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR
        , position: google.maps.ControlPosition.TOP_RIGHT
        , mapTypeIds: [
            google.maps.MapTypeId.ROADMAP
                    , google.maps.MapTypeId.SATELLITE
                    , google.maps.MapTypeId.HYBRID
                    , google.maps.MapTypeId.TERRAIN
        ]
    }
//        , navigationControl: true
    , streetViewControl: false
    , navigationControlOptions: {
        position: google.maps.ControlPosition.TOP_LEFT
//            , style: google.maps.NavigationControlStyle.ANDROID
        , style: google.maps.NavigationControlStyle.ZOOM_PAN
    }
//        , scaleControl: true
    , scaleControlOptions: {
        position: google.maps.ControlPosition.BOTTOM_LEFT
        , style: google.maps.ScaleControlStyle.DEFAULT
    }
};

function pintarmapa(divmapa) {
    map = new google.maps.Map(document.getElementById(divmapa), optionsmaps);
}
;

function createTooltip(marker, txt) {
    //create a tooltip 
    var tooltipOptions = {
        marker: marker,
        content: txt,
        cssClass: 'tooltip_googlemaps' // Nombre de la clase ha aplicar tooltip
    };
    var tooltip = new Tooltip(tooltipOptions);
}


function aniadirmarker(codestacion, codgoes, estacion, txt, sub_esta, lng, lat, pathicon, css, tipo, dpto, prov, dtrito) {

    if (pathicon == 'F') {
        pathicon = path + 'static/img/localizacion_verde.png';
    } else {
        pathicon = path + 'static/img/localizacion_rojo.png';
    }

    if (css == '' || css == undefined) {
        css = 'labelsgooglemaps';
    }

    var marker = new MarkerWithLabel({
        position: new google.maps.LatLng(lat, lng),
        draggable: false,
        map: map,
        //raiseOnDrag: true,
        icon: pathicon,
        zoomControl: true,
        //labelContent: estacion,
//	       labelAnchor: new google.maps.Point(100, 0),
        labelClass: css, //"labelsgooglemaps", // the CSS class for the label
        labelStyle: {opacity: 0},
        labelInBackground: true
    });
    //a�adiendo la ventana del evento hover
    createTooltip(marker, txt);

    //copiar datos a campos
    google.maps.event.addListener(marker, 'click', function () {
        estacion_datos(codestacion, estacion, sub_esta, tipo, dpto, prov, dtrito);
    });

    markersArray.push(marker);
}

function removermarkers(markersArray) {
    for (var i = 0; i < markersArray.length; i++) {
        markersArray[i].setMap(null);
    }
    markersArray.length = 0;
}
//
//
//INICIO CENTRAR MAPA
function centrarmapa() {
    if (markersArray.length > 0) {
        var limits = new google.maps.LatLngBounds();
        for (var marker = 0; marker < markersArray.length; marker++) {
            limits.extend(markersArray[marker].getPosition());
        }
        map.fitBounds(limits);
    } else {
        map.setOptions({zoom: startZoom, center: new google.maps.LatLng(centerLatitude, centerLongitude)});
    }
}
//FIN CENTRAR MAPA
//
//INICIO DETALLE DE LA ESTACI�N
function estacion_datos(codestacion, estacion, sub_esta, tipo, dpto, prov, dtrito){
    $('#txt_cod_estacion').val(codestacion);
    $('#lb_cod_estacion').addClass('active');
    $('#txt_estacion').val(estacion);
    $('#lb_estacion').addClass('active');
    $('#txt_dpto').val(dpto);
    $('#lb_dpto').addClass('active');
    $('#txt_provincia').val(prov+' / '+dtrito);
    $('#lb_provincia').addClass('active');
    $('#txt_tipo').val(sub_esta);
    $('#lb_tipo').addClass('active');
    $('#txt_categoria').val(tipo);
    $('#lb_categoria').addClass('active');    
    sgd_estacion_variables();
}
//FIN DETALLE DE LA ESTACI�N
//
//INICIO VARIABLES POR ESTACI�N
function sgd_estacion_variables(){
    var cod_estacion = $('#txt_cod_estacion').val();
    var des_estacion = $('#txt_estacion').val();
    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_variables_mostrar/",
            data:     "cod_estacion="+cod_estacion+
                      "&des_estacion="+des_estacion,
            beforeSend: function(data){
                $('#div_variables').html("Cargando...");
            },
            success: function(requestData){
                $('#div_variables').html(requestData);
                $('#div_variables').show();
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_variables').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN VARIABLES POR ESTACI�N
//
//INICIO ADICIONAR PEDIDO A SOLICITUD
function sgd_mant_add_solicitud(){
        
    var cod_estacion = $('#txt_cod_estacion').val();
    var id_var = $('#hd_id_var').val();
    $('#div_solicitud_detalle').show(); 
    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_solicitud_crear/",
            data:     "cod_estacion="+cod_estacion+
                      "&id_var="+id_var,
            beforeSend: function(data){
                $('#div_solicitud_detalle').html("Cargando...");
            },
            success: function(requestData){
                $('#div_solicitud_detalle').html(requestData);
                $('#div_solicitud_titulo').show();
                $('#div_solicitud_info').show();
                $('#div_motivo').show();
                $('#div_observacion').show();
                $('#div_enviar_sol_info').show();
                                
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_solicitud_detalle').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN ADICIONAR PEDIDO A SOLICITUD
//
//INICIO LISTA DE VARIABLES
function lista_variables(){
    var valor_var = "";
    var tempo = "";
    var temp_var = $('#hd_id_var').val(); 
    var temporal = "";
    
    $('.cb_variable:checked').each(function () {
        valor_var += $(this).val() + ",";
        tempo = $(this).val();
    
        if (temp_var == ''){
            var id_var_tmp = valor_var;
            $('#hd_id_var').val(id_var_tmp);
        }else{
            if (temp_var.indexOf(tempo) == -1){
                temporal += tempo + ',';
                var id_var_tmp = temp_var + temporal;
                $('#hd_id_var').val(id_var_tmp); 
                }
            }
    });   
}
//FIN LISTA DE DE VARIABLES
//
//INICIO MOSTRAR ESTACIONES POR DEPARTAMENTO
function sgd_mant_dpto_mostrar(){
    var cod_dpto = $('#cb_dpto').val();
    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_dpto_mostrar/",
            data:     "cod_dpto="+cod_dpto,
            beforeSend: function(data){
                $('#cb_estacion').html("<option>CARGANDO...</option>");
                $('#cb_estacion').selectpicker('refresh');
            },
            success: function(requestData){
                $('#cb_estacion').attr('disabled',false);
                $('#cb_estacion').html(requestData);
                $('#cb_estacion').selectpicker('refresh');
                sgd_mant_mapa_mostrar();
            },
            error: function(requestData, strError, strTipoError){
                $('#cb_estacion').html("<option>Error " + strTipoError +": " + strError+"</option>");
                $('#cb_estacion').selectpicker('refresh');
            }
        });
}
//FIN MOSTRAR ESTACIONES POR DEPARTAMENTO
//
//INICIO MOSTRAR SOLICITUD
function sgd_mant_solicitud_mostrar(){
//    var cod_procedimiento = $('#cb_servicio').val();
//    $('#div_solicitud_info').show();    
//    $('#div_motivo').show();
//    if (cod_procedimiento == '4'){//EXPEDICI�N DE LA INFORMACI�N
//        $('#div_mapa').show();
//        $('#div_solicitud_titulo').hide();
//        $('#div_solicitud_otros').hide();
//        $('#div_observacion').hide();
//        $('#div_solicitud_info').hide();
//        $('#div_enviar_sol_info').hide();
//        $('#div_enviar_sol_otros').hide();
//        $('#div_solicitud_tupa').hide();
//        $('#div_solicitud_tupa_detalle').hide();
//        $('#div_solicitud_rpta').hide();
//        $('#div_enviar_sol_tupa').hide();
//    }else if(cod_procedimiento == '2'){//TUPA
//        $('#div_solicitud_titulo').show();
//        $('#div_solicitud_otros').show();
//        $('#div_solicitud_tupa').show();
//        $('#div_solicitud_tupa_detalle').show();
//        $('#div_solicitud_rpta').show();
//        sgd_tipo_entrega_chkb();
//        sgd_rpta_email_chkb();
//        $('#div_enviar_sol_tupa').show();
//        $('#div_observacion').show();
//        $('#div_solicitud_info').hide();
//        $('#div_enviar_sol_otros').hide();
//        $('#div_mapa').hide();
//        $('#cb_estacion').attr('disabled', true);
//        $('#txt_cod_estacion').val('');
//        $('#txt_estacion').val('');
//        $('#txt_dpto').val('');
//        $('#txt_provincia').val('');
//        $('#txt_tipo').val('');
//        $('#txt_categoria').val('');
//        $('#div_variables').hide();
//        $('#div_nota').show();
//    }else if(cod_procedimiento == ''){
//        $('#hd_id').val('');
//        $('#txt_dni').val('');
//        $('#txt_dni').focus();
//        $('#txt_nombres').val('');
//        $('#txt_direccion').val('');
//        $('#txt_telefono').val('');
//        $('#div_per_natural').hide();
//        $('#div_per_juridica').hide();
//        $('#div_per_natural_buscar').show();
//        $('#div_per_juridica_buscar').hide();
//        $('#div_msg_re                                                                                                                                                                                                                                                        gistro').hide();
//        $('#div_guarda_ciudadano').hide();
//        $('#div_mapa').hide();
//        $('#div_cbo_servicio').hide();
//        $('#div_solicitud_titulo').hide();
//        $('#div_motivo').hide();
//        $('#div_solicitud_detalle').hide();
//        $('#div_solicitud_otros').hide();
//        $('#div_observacion').hide();
//        $('#div_enviar_sol_info').hide();
//        $('#div_enviar_sol_otros').hide();
//        $('#div_msg_registro_sol').hide();
//        $('#div_solicitud_tupa').hide();
//        $('#div_solicitud_tupa_detalle').hide();
//        $('#div_solicitud_rpta').hide();
//        $('#div_enviar_sol_tupa').hide();
//        $('#div_solicitud_tupa').hide();
//        $('#div_solicitud_tupa_detalle').hide();
//        $('#div_enviar_sol_tupa').hide();
//        $('#cb_estacion').attr('disabled', true);
//        $('#txt_cod_estacion').val('');
//        $('#txt_estacion').val('');
//        $('#txt_dpto').val('');
//        $('#txt_provincia').val('');
//        $('#txt_tipo').val('');
//        $('#txt_categoria').val('');
//        $('#div_variables').hide();
//    }else{
//        $('#div_solicitud_otros').show();
//        $('#div_mapa').hide();
//        $('#div_observacion').show();
//        $('#div_enviar_sol_info').hide();
//        $('#div_enviar_sol_otros').show();
//        $('#div_solicitud_titulo').show();
//        $('#div_solicitud_detalle').hide();
//        $('#div_solicitud_otros').show();
//        $('#div_solicitud_tupa').hide();
//        $('#div_solicitud_tupa_detalle').hide();
//        $('#div_solicitud_rpta').hide();
//        $('#div_enviar_sol_tupa').hide();
//        $('#cb_estacion').attr('disabled', true);
//        $('#txt_cod_estacion').val('');
//        $('#txt_estacion').val('');
//        $('#txt_dpto').val('');
//        $('#txt_provincia').val('');
//        $('#txt_tipo').val('');
//        $('#txt_categoria').val('');
//        $('#div_variables').hide();
//    }        
}
//FIN MOSTRAR SOLICITUD
//
//INICIO TIPO ENTREGA CHECKBOX
function sgd_tipo_entrega_chkb(){
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_tipo_entrega_chkb/",
            data:     "",
            beforeSend: function(data){
                $('#div_tipo_entrega_chkb').html("Cargando...");
            },
            success: function(requestData){
                $('#div_tipo_entrega_chkb').html(requestData);
                $('#div_tipo_entrega_chkb').show();                                
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_tipo_entrega_chkb').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN TIPO ENTREGA CHECKBOX
//
//INICIO TIPO ENTREGA CHECKBOX
function sgd_rpta_email_chkb(){
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_rpta_email_chkb/",
            data:     "",
            beforeSend: function(data){
                $('#div_rpta_email_chkb').html("Cargando...");
            },
            success: function(requestData){
                $('#div_rpta_email_chkb').html(requestData);
                $('#div_rpta_email_chkb').show();                                
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_rpta_email_chkb').html("Error " + strTipoError +": " + strError);
            }
        });
}
//FIN TIPO ENTREGA CHECKBOX
//
//INICIO GUARDAR SOLICITUD OTROS
function sgd_mant_enviar_solicitud_otros(){//DETERMINAR CAMPOS OBLIGATORIOS
    var id_sol = $('#hd_id_sol').val();
    var motivo = $('#txt_motivo').val();
    var proc = $('#cb_servicio').val();
    var descr = $('#txt_descripcion').val();
    var obs = $('#txt_observacion').val();
    var cod_adm = $('#hd_id').val();
    
    var msj_error = "";
    if (motivo.length == 0){
        msj_error += " el motivo de la solicitud";
    }
    if (descr.length == 0){
        msj_error += ", la descripci�n del servicio a solicitar.";
    }
    
    if(msj_error == ''){
        $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_solicitud_otros_guardar/",
            data:     "id_sol="+id_sol
                      +"&motivo="+motivo
                      +"&proc="+proc
                      +"&descr="+descr
                      +"&obs="+obs
                      +"&cod_adm="+cod_adm,
            beforeSend: function(data){
                $('#div_msg_registro_sol').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                var id  = arrayobj[0][0];
                var msj = arrayobj[0][1];//datos guardados
                
                $('#hd_id_sol').val(id);
                $.alert('<h6>' + msj + '</h6>');
            },
            error: function(requestData, strError, strTipoError){								
                $('#div_msg_registro_sol').html("Error " + strTipoError +": " + strError);
            }
        });
    }else{
         $.alert('<h6>Ingrese: ' + msj_error + '</h6>');
    }
}
//FIN GUARDAR SOLICITUD OTROS
//
//INICIO GUARDAR SOLICITUD TUSNE INFORMACI�N
function sgd_mant_enviar_solicitud_info(){
    var id_sol = $('#hd_id_sol').val();
    var motivo = $('#txt_motivo').val();
    var proc = $('#cb_servicio').val();
    var obs = $('#txt_observacion').val();
    var id_var = $('#hd_id_var').val();
    var cod_adm = $('#hd_id').val();
    
    var fec_ini = "";
        $('.fec_ini').each(function () {
            fec_ini += $(this).val() + ",";
        }); 
        fec_ini = fec_ini.substring(0, fec_ini.length - 1);
    var fec_fin = "";
        $('.fec_fin').each(function () {
            fec_fin += $(this).val() + ",";
        }); 
        fec_fin = fec_fin.substring(0, fec_fin.length - 1);
    var cad_var = "";
        $('.cad_variable').each(function () {
            cad_var += $(this).val() + ",";
        }); 
        cad_var = cad_var.substring(0, cad_var.length - 1);
    var cad_esc = "";
        $('.cb_escala').each(function () {
            cad_esc += $(this).val() + ",";
        }); 
        cad_esc = cad_esc.substring(0, cad_esc.length - 1);
    var cad_des_est = "";
        $('.cad_estacion').each(function () {
            cad_des_est += $(this).val() + ",";
        }); 
        cad_des_est = cad_des_est.substring(0, cad_des_est.length - 1);
    var cad_des_var = "";
        $('.cad_desvariable').each(function () {
            cad_des_var += $(this).val() + ",";
        }); 
        cad_des_var = cad_des_var.substring(0, cad_des_var.length - 1);
    
    var msj_error = "";
    if (motivo.length == 0){
        msj_error += " Ingrese el motivo de la solicitud.";
    }
    
    if(msj_error == ''){
        $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_solicitud_info_guardar/",
            data:     "id_sol="+id_sol
                      +"&motivo="+motivo
                      +"&proc="+proc
                      +"&obs="+obs
                      +"&cod_adm="+cod_adm
                      +"&cad_var="+cad_var
                      +"&fec_ini="+fec_ini
                      +"&fec_fin="+fec_fin
                      +"&cad_esc="+cad_esc
                      +"&cad_des_est="+cad_des_est
                      +"&cad_des_var="+cad_des_var,
            beforeSend: function(data){
                $('#div_msg_registro_sol').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                var id  = arrayobj[0][0];
                var msj = arrayobj[0][1];//datos guardados
                
                $('#hd_id_sol').val(id);
                $.alert('<h6>' + msj + '</h6>');
            },
            error: function(requestData, strError, strTipoError){
                $('#div_msg_registro_sol').html("Error " + strTipoError +": " + strError);
            }
        });
    }else{
         $.alert('<h6>Ingrese: ' + msj_error + '</h6>');
    }
}
//FIN GUARDAR SOLICITUD TUSNE INFORMACI�N
//
//INICIO GUARDAR SOLICITUD TUPA*******************************************************************
function sgd_mant_enviar_solicitud_tupa(){
    var id_sol = $('#hd_id_sol').val();
    var cod_adm = $('#hd_id').val();
    var descr = $('#txt_infosol').val();
    var funcionario = $('#cb_funcionario').val();
    var obs = $('#txt_observacion').val();
    
    var tipo_entr = "";
    $('.cb_tipoentr:checked').each(function () {
        tipo_entr += $(this).val() + ",";
    });
    tipo_entr = tipo_entr.substring(0,tipo_entr.length - 1);
    
    var rpta = "";
    $('.rb_rpta:checked').each(function () {
        rpta =  $(this).attr("cod");
    });
            
    var msj_error = "";
    if (descr.length == 0){
        msj_error += " La informaci�n solicitada." + "<br>";
    }
    if (tipo_entr.length == 0){
        msj_error += " C�mo desea recibir la informaci�n." + "<br>";
    }
    if (rpta.length == 0){
        msj_error += " Si desea o no recibir la respuesta por E-Mail.";
    }
    
    if(msj_error == ''){
        $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_solicitud_tupa_guardar/",
            data:     "id_sol="+id_sol
                      +"&descr="+descr
                      +"&obs="+obs
                      +"&cod_adm="+cod_adm
                      +"&funcionario="+funcionario
                      +"&tipo_entr="+tipo_entr
                      +"&rpta="+rpta,
            beforeSend: function(data){
                $('#div_msg_registro_sol').html("Cargando...");
            },
            success: function(requestData){
                arrayobj = jQuery.parseJSON(requestData);
                var id  = arrayobj[0][0];
                var msj = arrayobj[0][1];//datos guardados
                
                $('#hd_id_sol').val(id);
                $.alert('<h6>' + msj + '</h6>');
            },
            error: function(requestData, strError, strTipoError){
                $('#div_msg_registro_sol').html("Error " + strTipoError +": " + strError);
            }
        });
    }else{
//         $.alert('<h6>' + msj_error + '</h6>');
        $.confirm({
            title: 'ERROR!',
            content: '<h6>Indique: <br>' + msj_error + '</h6>',
            type: 'red',
            typeAnimated: true,
            buttons: {
                cerrar: {
                    text: 'Cerrar',
                    btnClass: 'btn-red',
                    action: function(){
                    }
                }
            }
        });
    }    
}
//FIN GUARDAR SOLICITUD TUPA*******************************************************************
//
//INICIO BUSCAR PROVINCIA
function sgd_mant_provincia_mostrar(tipo_doc){
    var cod_dpto = "";
    if (tipo_doc === 1){
        cod_dpto = $('#cb_dpto_dir').val();
    }else if (tipo_doc === 2){
        cod_dpto = $('#cb_dpto_dir_ruc').val();
    }        
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_provincia_mostrar/",
            data:     "cod_dpto="+cod_dpto,
            beforeSend: function(data){
                if (tipo_doc === 1){
                    $('#cb_prov_dir').html("Cargando...");
                    $('#cb_prov_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_prov_dir_ruc').html("Cargando...");
                    $('#cb_prov_dir_ruc').selectpicker('refresh');
                }
            },
            success: function(requestData){
                if (tipo_doc === 1){
                    $('#cb_prov_dir').html(requestData);
                    $('#cb_prov_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_prov_dir_ruc').html(requestData);
                    $('#cb_prov_dir_ruc').selectpicker('refresh');
                }
            },
            error: function(requestData, strError, strTipoError){
                if (tipo_doc === 1){
                    $('#cb_prov_dir').html("Error " + strTipoError +": " + strError);
                    $('#cb_prov_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_prov_dir_ruc').html("Error " + strTipoError +": " + strError);
                    $('#cb_prov_dir_ruc').selectpicker('refresh');
                }
            }
        });
}
//FIN BUSCAR PROVINCIA
//
//INICIO BUSCAR DISTRITO
function sgd_mant_distrito_mostrar(tipo_doc){
    var cod_prov = "";
    var cod_dpto = "";
    if (tipo_doc === 1){
        cod_prov = $('#cb_prov_dir').val();
        cod_dpto = $('#cb_dpto_dir').val();
    }else if (tipo_doc === 2){
        cod_prov = $('#cb_prov_dir_ruc').val();
        cod_dpto = $('#cb_dpto_dir_ruc').val();
    }    
    $.ajax({
            dataType: "html",
            type:     "GET",
            url:      path + "sgd/mant_distrito_mostrar/",
            data:     "cod_prov="+cod_prov
                      +"&cod_dpto="+cod_dpto,
            beforeSend: function(data){
                if (tipo_doc === 1){
                    $('#cb_dist_dir').html("Cargando...");
                    $('#cb_dist_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_dist_dir_ruc').html("Cargando...");
                    $('#cb_dist_dir_ruc').selectpicker('refresh');
                }
            },
            success: function(requestData){
                if (tipo_doc === 1){
                    $('#cb_dist_dir').html(requestData);
                    $('#cb_dist_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_dist_dir_ruc').html(requestData);
                    $('#cb_dist_dir_ruc').selectpicker('refresh');
                }
            },
            error: function(requestData, strError, strTipoError){
                if (tipo_doc === 1){
                    $('#cb_dist_dir').html("Error " + strTipoError +": " + strError);
                    $('#cb_dist_dir').selectpicker('refresh');
                }else if (tipo_doc === 2){
                    $('#cb_dist_dir').html("Error " + strTipoError +": " + strError);
                    $('#cb_dist_dir').selectpicker('refresh');
                }
            }
        });
}
//FIN BUSCAR DISTRITO
//
//


