<div class="row">
    <div class="input-field col-sm-1"></div>
    <div class="alert alert-danger col-sm-10" id="alerta">
        <strong>Alerta! Debe seleccionar Tipo de Plantilla, Direcci�n Zonal y Estaci�n</strong>
    </div>
    <div class="input-field col-sm-1"></div>
</div>
        
<div class="row">
    <div class="input-field col-sm-1"></div>
    <div class="input-field col-sm-3">
        <select name="cb_tipo_planilla" id="cb_tipo_planilla" class="form-control selectpicker" onchange="cod_tplanilla(this.options[this.selectedIndex].value);" data-live-search="true"></select>
        <label for="cb_tipo_planilla" class="active">Tipo planilla</label>
        <input type="hidden" id="hd_tplanilla" value="">
    </div>
    <div class="input-field col-sm-3">
        <select name="cb_dz" id="cb_dz" class="form-control selectpicker" onchange="sisgem_buscar_plantilla_cargar_cbestaciones(); cod_dz(this.options[this.selectedIndex].value);" data-live-search="true"></select>
        <label for="cb_dz" class="active">Direcci�n Zonal</label>
        <input type="hidden" id="hd_dz" value="">
    </div>
    <div class="input-field col-sm-3">
        <select name="cb_estacion" id="cb_estacion" class="form-control selectpicker" data-live-search="true" onchange="cod_esta(this.options[this.selectedIndex].value);"></select>
        <label for="cb_estacion" class="active">Estaci�n</label>
        <input type="hidden" id="hd_esta" value="">
    </div>
    <div class="input-field col-sm-1">
        <!--<button type="button" class="btn btn-primary" onclick="sisgem_buscar_data_js()">BUSCAR</button>-->
        <button type="button" class="btn btn-primary" onclick="sisgem_validacbo_busarpl_js()">BUSCAR</button>
    </div>
    <div class="input-field col-sm-1"></div>
</div>

<br>

<div class="row col-sm-12">
    <div class="input-field col-sm-1">        
    </div>
    <div class="col-sm-9">
        <div id="tabla_data">
        </div>
    </div>
    <div class="col-sm-2 text-left" id="div_leyenda">
        <p>Leyenda:</p>
        <div class="row">
            <button type="button" class="btn btn-success" style=" background-color: #40a742"><span class="glyphicon glyphicon-search"></span></button>
            Planilla digitalizada y digitada
        </div>
        <div class="row">
            <button type="button" class="btn btn-info"><span class="glyphicon glyphicon-search"></span></button>
            Planilla digitalizada
        </div>
        <div class="row">
            <button type="button" class="btn btn-warning"><span class="glyphicon glyphicon-list-alt"></span></button>
            Planilla digitada
        </div>
        <div class="row">
            <button type="button" class="btn btn-white"><span class="glyphicon glyphicon-ban-circle"></span></button>
            Sin informaci�n
        </div>
    </div>
</div>

<!---------------- Modal ---------------->
  <div class="modal fade" id="modal_visualiza" role="dialog">
    <div class="modal-dialog modal-md">    
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h6 class="modal-title" id="lb_title_modal_tplanilla"></h6>
              <div class="dz">
                  <h6 class="modal-title" id="lb_title_modal_dz"></h6> 
              </div>                       
              <h6 class="modal-title" id="lb_title_modal_esta"></h6>          
            </div>
            <div class="modal-body">
                <div id="imagenes" class="row">                            
                </div>
<!--                <div id="div_alerta" class="row">
                    Cargando
                </div>-->
                 
                
            </div>        
        </div>      
    </div> 
  </div>
<!---------------- Modal ---------------->

<script>
   sisgem_buscar_tipoplantilla_js();
   $('#alerta').hide();
   
    function cod_tplanilla(cod_tplanilla){
       $('#hd_tplanilla').val(cod_tplanilla);
    };
    function cod_dz(cod_dz){
       $('#hd_dz').val(cod_dz);
    };
    function cod_esta(cod_esta){
       $('#hd_esta').val(cod_esta);
    };
    
    $('#div_leyenda').hide();
   
</script>


   