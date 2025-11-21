package br.com.nexo.control;

import br.com.nexo.config.JwtUtil;
import br.com.nexo.dto.LoginRequest;
import br.com.nexo.model.Usuario;
import br.com.nexo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // login usado pelo app: POST /api/auth/login
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNmEmail(),
                            loginRequest.getNmSenha()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // carrega o usuário completo do banco
        Usuario usuario = usuarioRepository
                .findByNmEmail(loginRequest.getNmEmail())
                .orElse(null);

        // opcional: não expor a senha na resposta
        if (usuario != null) {
            usuario.setNmSenha(null);
            if (usuario.getFuncoes() != null) {
                usuario.getFuncoes().forEach(f -> f.setUsuarios(null));
            }
        }

        final UserDetails userDetails =
                userDetailsService.loadUserByUsername(loginRequest.getNmEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("usuario", usuario);

        return ResponseEntity.ok(response);
    }
}
