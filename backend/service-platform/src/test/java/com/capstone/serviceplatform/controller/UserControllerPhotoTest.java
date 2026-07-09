package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerPhotoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @TempDir
    private Path tempDir;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @SuppressWarnings("unchecked")
    void uploadPhotoPersistsFinalUrlAndReturnsAndroidFields() throws Exception {
        ReflectionTestUtils.setField(userController, "uploadDir", tempDir.toString());

        User user = new User();
        user.setId(42L);
        user.setEmail("client@test.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setScheme("http");
        servletRequest.setServerName("localhost");
        servletRequest.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "avatar.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        MultipartHttpServletRequest multipartRequest = mock(MultipartHttpServletRequest.class);
        when(multipartRequest.getFile("file")).thenReturn(null);
        when(multipartRequest.getFile("photo")).thenReturn(photo);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = userController.uploadPhoto(user.getId(), multipartRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).saveAndFlush(user);

        String savedPhoto = user.getPhoto();
        assertNotNull(savedPhoto);
        assertTrue(savedPhoto.startsWith("http://localhost:8080/uploads/"));

        String savedFileName = savedPhoto.substring(savedPhoto.lastIndexOf('/') + 1);
        assertTrue(Files.exists(tempDir.resolve(savedFileName)));

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(savedPhoto, body.get("photo"));
        assertEquals(savedPhoto, body.get("photoUrl"));
    }
}
