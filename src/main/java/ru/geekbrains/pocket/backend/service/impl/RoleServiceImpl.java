package ru.geekbrains.pocket.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.Privilege;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.repository.PrivilegeRepository;
import ru.geekbrains.pocket.backend.repository.RoleRepository;
import ru.geekbrains.pocket.backend.service.RoleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    public Role createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            List<Privilege> privileges;
            final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
            final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
            final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");
            final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
            final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));

            if (name.equals("ROLE_ADMIN"))
                privileges = adminPrivileges;
            else
                privileges = userPrivileges;

            role = new Role(name);
            role.setPrivileges(privileges);
            role = roleRepository.insert(role);
        }
        return role;
    }

    @Override
    public Role createRoleIfNotFound(String name, List<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            role = roleRepository.insert(role);
        }
        return role;
    }

    @Override
    public Role getRoleAdmin() {
        return createRoleIfNotFound("ROLE_ADMIN");
    }

    @Override
    public Role getRoleUser() {
        return createRoleIfNotFound("ROLE_USER");
    }

    @Override
    public Role getRole(String rolename) {
        return roleRepository.findByName(rolename);
    }

    @Override
    public Role insert(Role role) {
        return roleRepository.insert(role);
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    private Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

}
