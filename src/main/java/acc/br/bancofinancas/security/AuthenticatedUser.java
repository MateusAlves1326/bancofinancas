package acc.br.bancofinancas.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import acc.br.bancofinancas.model.Role;

public class AuthenticatedUser implements UserDetails {

    private final String username;
    private final String password;
    private final Role role;
    private final Integer clienteId;
    private final Integer agenciaId;

    public AuthenticatedUser(String username, String password, Role role, Integer clienteId, Integer agenciaId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.clienteId = clienteId;
        this.agenciaId = agenciaId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public Integer getAgenciaId() {
        return agenciaId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
