package vn.tuhoc.UserService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;

public interface UsernameRepository
                extends JpaRepository<UsernameEntity, Integer>, JpaSpecificationExecutor<UsernameEntity> {
        Boolean existsByValue(String value);

        Optional<UsernameEntity> findByTypeAndValue(UsernameTypeEnum type, String value);
}
