package org.isf.vaccine.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.vaccine.dto.VaccineDTO;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.mapper.VaccineMapper;
import org.isf.vaccine.model.Vaccine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class VaccineController {

    private final Logger logger = LoggerFactory.getLogger(VaccineController.class);

    @Autowired
    protected VaccineBrowserManager vaccineManager;
    
    @Autowired
    protected VaccineMapper mapper;

    public VaccineController(VaccineBrowserManager vaccineManager, VaccineMapper vaccineMapper) {
        this.vaccineManager = vaccineManager;
        this.mapper = vaccineMapper;
    }

    /**
     * Get all the vaccines stored
     *
     * @return NO_CONTENT if there aren't vaccines, List<VaccineDTO> otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccines() throws OHServiceException {
        logger.info("Get vaccines");
        ArrayList<Vaccine> vaccines = vaccineManager.getVaccine();
        List<VaccineDTO> listVaccines = mapper.map2DTOList(vaccines);
        if (listVaccines.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    /**
     * Get all the vacccines related to a vaccineType code
     *
     * @param vaccineTypeCode of the vaccine
     * @return NO_CONTENT if there aren't vaccines related to code, List<VaccineDTO> otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines/{vaccineTypeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VaccineDTO>> getVaccinesByVaccineTypeCode(@PathVariable String vaccineTypeCode) throws OHServiceException {
        logger.info("Get vaccine by code:" + vaccineTypeCode);
        ArrayList<Vaccine> vaccines = vaccineManager.getVaccine(vaccineTypeCode);
        List<VaccineDTO> listVaccines = mapper.map2DTOList(vaccines);
        if (listVaccines.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listVaccines);
        } else {
            return ResponseEntity.ok(listVaccines);
        }
    }

    /**
     * Create new vaccine
     *
     * @param newVaccine
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newVaccine(@RequestBody VaccineDTO newVaccine) throws OHServiceException {
        logger.info("Create vaccine: " + newVaccine.toString());
        boolean isCreated;
        try {
             isCreated = vaccineManager.newVaccine(mapper.map2Model(newVaccine));
        } catch (OHDataIntegrityViolationException e) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine type already present!", OHSeverityLevel.ERROR));
        }
        if (!isCreated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * Update vaccine
     *
     * @param updateVaccine
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @PutMapping(value = "/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateVaccine(@RequestBody VaccineDTO updateVaccine) throws OHServiceException {
        logger.info("Update vaccine: " + updateVaccine.toString());
        boolean isUpdated = vaccineManager.updateVaccine(mapper.map2Model(updateVaccine));
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(null);

    }

    /**
     * Delete vaccine
     *
     * @param code of the vaccine to delete
     * @return an error message if there are some problem, ok otherwise
     * @throws OHServiceException
     */
    @DeleteMapping(value = "/vaccines/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteVaccine(@PathVariable("code") String code) throws OHServiceException {
        logger.info("Delete vaccine code: {}", code);
        boolean isDeleted = false;
        Vaccine vaccine = vaccineManager.findVaccine(code);
        if (vaccine!=null){
            isDeleted = vaccineManager.deleteVaccine(vaccine);
            if (!isDeleted) {
                throw new OHAPIException(new OHExceptionMessage(null, "Vaccine is not deleted!", OHSeverityLevel.ERROR));
            }
            return ResponseEntity.ok(isDeleted);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    /**
     * Check if code is already use by other vaccine
     *
     * @param code
     * @return true if it is already use, false otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/vaccines/check/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkVaccineCode(@PathVariable String code) throws OHServiceException {
        logger.info("Check vaccine code: " + code);
        boolean check = vaccineManager.codeControl(code);
        return ResponseEntity.ok(check);
    }
}