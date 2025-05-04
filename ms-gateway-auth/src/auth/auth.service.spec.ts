jest.mock('bcryptjs', () => ({compare: jest.fn(),}));

import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { UsersService } from '../users/users.service';
import { JwtService } from '@nestjs/jwt';
import { UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcryptjs';

describe('AuthService', () => {
  let authService: AuthService;
  let usersService: UsersService;
  let jwtService: JwtService;
  const mockUsersService = {obtenerPorEmail: jest.fn(), };
  const mockJwtService = {sign: jest.fn(),};

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,{provide: UsersService,useValue: mockUsersService,},
        {provide: JwtService,useValue: mockJwtService,},
      ],}).compile();

    authService = module.get<AuthService>(AuthService);
    usersService = module.get<UsersService>(UsersService);
    jwtService = module.get<JwtService>(JwtService);
    jest.clearAllMocks();
  });

  describe('validateUser', () => {
    it('debe retornar el payload del usuario cuando las credenciales son válidas', async () => {
      const email = 'test@example.com';
      const password = 'password123';
      const user = { id: 1, email, passwordHash: 'hashPassword', rol: 'admin' };
      mockUsersService.obtenerPorEmail.mockResolvedValue(user);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);
      const payload = await authService.validateUser(email, password);
      expect(payload).toEqual({ sub: user.id, email: user.email, rol: user.rol });
      expect(mockUsersService.obtenerPorEmail).toHaveBeenCalledWith(email);
      expect(bcrypt.compare).toHaveBeenCalledWith(password, user.passwordHash);
    });

    it('debe lanzar UnauthorizedException cuando la contraseña es inválida', async () => {
      const email = 'test@example.com';
      const password = 'wrongPassword';
      const user = { id: 1, email, passwordHash: 'hashPassword', rol: 'user' };
      mockUsersService.obtenerPorEmail.mockResolvedValue(user);
      (bcrypt.compare as jest.Mock).mockResolvedValue(false);
      await expect(authService.validateUser(email, password))
        .rejects
        .toThrow(UnauthorizedException);
      expect(mockUsersService.obtenerPorEmail).toHaveBeenCalledWith(email);
      expect(bcrypt.compare).toHaveBeenCalledWith(password, user.passwordHash);
    });
  });

  describe('login', () => {
    it('debe retornar un accessToken cuando las credenciales son válidas', async () => {
      const email = 'test@example.com';
      const password = 'password123';
      const payload = { sub: 1, email, rol: 'admin' };
      const fakeToken = 'fakeJwtToken';
      jest.spyOn(authService, 'validateUser').mockResolvedValue(payload);
      mockJwtService.sign.mockReturnValue(fakeToken);
      const result = await authService.login(email, password);
      expect(result).toEqual({ accessToken: fakeToken });
      expect(authService.validateUser).toHaveBeenCalledWith(email, password);
      expect(mockJwtService.sign).toHaveBeenCalledWith(payload);
    });

    it('debe propagar la excepción cuando validateUser lanza error', async () => {
      const email = 'test@example.com';
      const password = 'wrongPassword';
      const error = new UnauthorizedException('Credenciales incorrectas');
      jest.spyOn(authService, 'validateUser').mockRejectedValue(error);
      await expect(authService.login(email, password)).rejects.toThrow(UnauthorizedException);
      expect(authService.validateUser).toHaveBeenCalledWith(email, password);
    });
  });
});