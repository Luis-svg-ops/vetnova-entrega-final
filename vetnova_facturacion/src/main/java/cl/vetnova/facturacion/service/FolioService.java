package cl.vetnova.facturacion.service;

import cl.vetnova.facturacion.dto.FolioRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.Folio;
import cl.vetnova.facturacion.repository.FolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class FolioService {

    private static final Set<String> TIPOS = Set.of("BOLETA", "FACTURA");
    private static final String TIPO_INVALIDO = "Tipo no válido. Valores permitidos: BOLETA, FACTURA";
    private static final int UMBRAL_DEFAULT = 10;

    private final FolioRepository folioRepository;

    public FolioService(FolioRepository folioRepository) {
        this.folioRepository = folioRepository;
    }

    @Transactional(readOnly = true)
    public List<Folio> listar() {
        return folioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Folio buscar(Long id) {
        return folioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado"));
    }

    @Transactional
    public Folio crear(FolioRequest request) {
        if (request.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        // La existencia de la sucursal vive en MS Auth/Sucursales → verificación diferida.
        if (request.getTipoDocumento() == null) {
            throw new BusinessRuleException("El tipo de documento es obligatorio");
        }
        if (!TIPOS.contains(request.getTipoDocumento())) {
            throw new BusinessRuleException(TIPO_INVALIDO);
        }
        if (request.getFolioDesde() == null) {
            throw new BusinessRuleException("El folio inicial es obligatorio");
        }
        if (request.getFolioDesde() <= 0) {
            throw new BusinessRuleException("El folio inicial debe ser mayor a 0");
        }
        if (request.getFolioHasta() == null) {
            throw new BusinessRuleException("El folio final es obligatorio");
        }
        if (request.getFolioHasta() < request.getFolioDesde()) {
            throw new BusinessRuleException("El folio final debe ser mayor o igual al folio inicial");
        }
        for (Folio existente : folioRepository.findBySucursalAndTipoDocumento(request.getSucursal(), request.getTipoDocumento())) {
            if (existente.getFolioDesde() <= request.getFolioHasta() && request.getFolioDesde() <= existente.getFolioHasta()) {
                throw new ConflictException("El rango de folios se superpone con uno existente");
            }
        }
        Folio folio = new Folio();
        folio.setSucursal(request.getSucursal());
        folio.setTipoDocumento(request.getTipoDocumento());
        folio.setFolioDesde(request.getFolioDesde());
        folio.setFolioHasta(request.getFolioHasta());
        folio.setFolioActual(request.getFolioDesde());
        folio.setFoliosRestantes(request.getFolioHasta() - request.getFolioDesde() + 1);
        folio.setActivo(true);
        folio.setUmbral(UMBRAL_DEFAULT);
        return folioRepository.save(folio);
    }

    @Transactional
    public Integer getSiguienteFolio(Long id) {
        Folio folio = buscar(id);
        if (folio.getFoliosRestantes() == 0) {
            throw new BusinessRuleException("No hay folios disponibles");
        }
        if (!Boolean.TRUE.equals(folio.getActivo())) {
            throw new BusinessRuleException("El rango de folios está inactivo");
        }
        Integer siguiente = folio.getFolioActual();
        folio.setFolioActual(siguiente + 1);
        folio.setFoliosRestantes(folio.getFoliosRestantes() - 1);
        folioRepository.save(folio);
        return siguiente;
    }

    @Transactional(readOnly = true)
    public boolean requiereAlerta(Long id) {
        Folio folio = buscar(id);
        return folio.getFoliosRestantes() < folio.getUmbral();
    }
}
