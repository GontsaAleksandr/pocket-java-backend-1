package ru.geekbrains.pocket.backend.service;

import ru.geekbrains.pocket.backend.domain.db.Privilege;
import ru.geekbrains.pocket.backend.domain.db.Role;

import java.util.List;

//this class for Spring Security

public interface RoleService {

    Role createRoleIfNotFound(final String name);

    Role createRoleIfNotFound(final String name, final List<Privilege> privileges);

    Role getRoleAdmin();

    Role getRoleUser();

    Role getRole(String roleName);

    Role insert(Role role);

    Role save(Role role);
}
