<input type="hidden" name="hd_cad_id_exp" id="hd_cad_id_exp" value="${requestScope['cad_id_exp']}" />
<br>
<div class="row">
    <div class="col-sm-12">
        <div class="row">
            <div class="input-field col-sm-1">
            </div>
            <div class="input-field col-sm-10" id="div_mant_lista" >
                ${requestScope['tbl_exp']}
                ${requestScope['tbl_exp_p']}
            </div>
            <div class="input-field col-md-1">
            </div>
        </div>
    </div>
</div>
<br>
<br>
<div class="row">
    <div class="col-md-12">
        <div class="row">
            <div class="input-field col-md-1">
            </div>
            <div class="input-field text-right col-md-10">
                <button onclick="$.colorbox.close()" class="btn btn-info btn-sm">Cerrar</button>
            </div>
            <div class="input-field col-md-1">
            </div>
        </div>
    </div>
</div>

<script>
//    $('#file-pad').fileinput({
//        language: 'es',
//        uploadUrl: '#',
//        allowedFileExtensions: ['pdf'],
//        showUpload: false,
//        //mainClass: "input-group-lg",
//        rtl: true
//    });    
//    
//    $('#cb_etapa option:first-child').attr('selected', 'selected');
//    $('#div_mant_adjunto_tbl').toggle();    
    
</script>            