package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByPartnerId(Long partnerId);

    List<Address> findByPartnerIdAndAddressType(
            Long partnerId,
            AddressType addressType
    );

    List<Address> findByPartnerUserEmail(String email);
}