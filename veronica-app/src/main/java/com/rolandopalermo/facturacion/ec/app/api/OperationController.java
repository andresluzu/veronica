package com.rolandopalermo.facturacion.ec.app.api;

import com.rolandopalermo.facturacion.ec.dto.CertificadoDigitalDTO;
import com.rolandopalermo.facturacion.ec.dto.EmpresaDTO;
import com.rolandopalermo.facturacion.ec.dto.MetodoPagoDTO;
import com.rolandopalermo.facturacion.ec.dto.PasswordDTO;
import com.rolandopalermo.facturacion.ec.dto.TipoDocumentoDTO;
import com.rolandopalermo.facturacion.ec.dto.TipoImpuestoDTO;
import com.rolandopalermo.facturacion.ec.dto.UsuarioDTO;
import com.rolandopalermo.facturacion.ec.dto.VeronicaResponseDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.DigitalCert;
import com.rolandopalermo.facturacion.ec.persistence.entity.PaymentMethod;
import com.rolandopalermo.facturacion.ec.persistence.entity.ReceiptType;
import com.rolandopalermo.facturacion.ec.persistence.entity.Supplier;
import com.rolandopalermo.facturacion.ec.persistence.entity.TaxType;
import com.rolandopalermo.facturacion.ec.persistence.entity.User;
import com.rolandopalermo.facturacion.ec.service.GenericCRUDService;
import com.rolandopalermo.facturacion.ec.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.rolandopalermo.facturacion.ec.common.util.Constants.API_DOC_ANEXO_1;
import static com.rolandopalermo.facturacion.ec.common.util.Constants.URI_OPERATIONS;

@RestController
@RequestMapping(value = {URI_OPERATIONS})
@Api(description = "Realiza operaciones generales sobre Verónica")
public class OperationController {

    @Autowired
    @Qualifier("digitalCertServiceImpl")
    private GenericCRUDService<DigitalCert, CertificadoDigitalDTO> digitalCertService;

    @Autowired
    @Qualifier("paymentMethodServiceImpl")
    private GenericCRUDService<PaymentMethod, MetodoPagoDTO> paymentMethodService;

    @Autowired
    @Qualifier("taxTypeServiceImpl")
    private GenericCRUDService<TaxType, TipoImpuestoDTO> taxTypeService;

    @Autowired
    @Qualifier("receiptTypeServiceImpl")
    private GenericCRUDService<ReceiptType, TipoDocumentoDTO> receiptTypeService;

    @Autowired
    @Qualifier("userServiceImpl")
    private GenericCRUDService<User, UsuarioDTO> userService;

    @Autowired
    @Qualifier("supplierServiceImpl")
    private GenericCRUDService<Supplier, EmpresaDTO> supplierService;

    @ApiOperation(value = "Almacena un certificado digital asociado a número de RUC")
    @PostMapping(value = "certificados-digitales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertDigitalCert(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody CertificadoDigitalDTO certificadoDigital) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        digitalCertService.saveOrUpdate(certificadoDigital);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Borra un certificado digital asociado a número de RUC")
    @DeleteMapping(value = "borrar-certificado-digital", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDigitalCert(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody String rucPropietario) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        digitalCertService.deleteExistingByRUC(rucPropietario);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Crea un nuevo tipo de impuesto")
    @PostMapping(value = "tipos-impuesto", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertTaxType(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody TipoImpuestoDTO tipoImpuestoDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        taxTypeService.saveOrUpdate(tipoImpuestoDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Crea un nuevo tipo de documento")
    @PostMapping(value = "tipos-documento", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertReceiptType(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody TipoDocumentoDTO tipoDocumentoRetenidoDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        receiptTypeService.saveOrUpdate(tipoDocumentoRetenidoDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Crea un nuevo método de pago")
    @PostMapping(value = "metodos-pago", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertPaymentMethod(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody MetodoPagoDTO metodoPagoDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        paymentMethodService.saveOrUpdate(metodoPagoDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Crea un nuevo usuario de la aplicación")
    @PostMapping(value = "usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertUser(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody UsuarioDTO usuarioDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        userService.saveOrUpdate(usuarioDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Actualiza la contraseña de un usuario de la aplicación")
    @PutMapping(value = "usuarios/{usuario}/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePassword(
            @Valid @ApiParam(value = "Nombre de usuario", required = true) @PathVariable("usuario") String usuario,
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody PasswordDTO passwordDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        ((UserServiceImpl) userService).updatePassword(usuario, passwordDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Registra datos de una nueva empresa")
    @PostMapping(value = "empresa", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> insertSupplier(
            @Valid @ApiParam(value = API_DOC_ANEXO_1, required = true) @RequestBody EmpresaDTO empresaDTO) {
        VeronicaResponseDTO<Object> response = new VeronicaResponseDTO<>();
        supplierService.saveOrUpdate(empresaDTO);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Retorna la información asociada a una empresa")
    @GetMapping(value = "empresa/{numeroIdentificacion}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getBolsBySupplier(
            @Valid @ApiParam(value = "Número de identificación de la empresa", required = true) @PathVariable("numeroIdentificacion") String numeroIdentificacion) {
        VeronicaResponseDTO<List<EmpresaDTO>> response = new VeronicaResponseDTO<>();
        EmpresaDTO empresa = new EmpresaDTO();
        empresa.setNumeroIdentificacion(numeroIdentificacion);
        List<EmpresaDTO> listSuppliers = supplierService.findAll(empresa);
        if (!StringUtils.isEmpty(listSuppliers)) {
            response.setResult(listSuppliers);
            response.setSuccess(true);
            return (new ResponseEntity<Object>(response, HttpStatus.OK));
        } else {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
    }

}