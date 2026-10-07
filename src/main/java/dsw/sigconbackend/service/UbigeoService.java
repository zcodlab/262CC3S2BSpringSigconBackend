package dsw.sigconbackend.service;

import dsw.sigconbackend.model.Ubigeo;
import dsw.sigconbackend.repository.UbigeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbigeoService {
    @Autowired
    UbigeoRepository ubigeoRepository;

    public List<Ubigeo> getUbigeo(){
        return ubigeoRepository.findAll();
    }
}
