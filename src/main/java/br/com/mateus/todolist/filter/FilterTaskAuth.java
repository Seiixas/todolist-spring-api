package br.com.mateus.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.mateus.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks/")) {
      var authorization = request.getHeader("Authorization");

      var authEncoded = authorization.substring("Basic".length()).trim();
      var authDecoded = Base64.getDecoder().decode(authEncoded);

      var auth = new String(authDecoded);

      var credentials = auth.split(":");
      var username = credentials[0];
      var password = credentials[1];

      var user = this.userRepository.findByUsername(username);

      if (user.isEmpty()) {
        response.sendError(401);
      } else {
        var passwordMatches = BCrypt.verifyer().verify(password.toCharArray(), user.get().getPassword());
        if (passwordMatches.verified) {
          request.setAttribute("idUser", user.get().getId());
          filterChain.doFilter(request, response);
        } else {
          response.sendError(401);
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
