import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { LoginDTO } from './auth.dto';

describe('AuthController', () => {
  let controller: AuthController;
  let authService: AuthService;

  const mockAuthService = {
    login: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuthController],
      providers: [
        {
          provide: AuthService,
          useValue: mockAuthService,
        },
      ],
    }).compile();

    controller = module.get<AuthController>(AuthController);
    authService = module.get<AuthService>(AuthService);
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('login', () => {

    it('debe devolver el token de autenticación cuando las credenciales son válidas', async () => {
      const loginDto: LoginDTO = { email: 'test@example.com', password: '123456' };
      const expectedResponse = { token: 'jwtTokenExample' };
      mockAuthService.login.mockResolvedValue(expectedResponse);
      const result = await controller.login(loginDto);
      expect(result).toEqual(expectedResponse);
      expect(authService.login).toHaveBeenCalledWith(loginDto.email, loginDto.password);
    });

    it('debe propagar el error cuando las credenciales son inválidas', async () => {
      const loginDto: LoginDTO = { email: 'invalid@example.com', password: 'wrongPassword' };
      const error = new Error('Credenciales inválidas');
      mockAuthService.login.mockRejectedValue(error);
      await expect(controller.login(loginDto)).rejects.toThrow('Credenciales inválidas');
      expect(authService.login).toHaveBeenCalledWith(loginDto.email, loginDto.password);
    });

    it('debe manejar la respuesta inesperada (undefined) del servicio', async () => {
      const loginDto: LoginDTO = { email: 'test@example.com', password: '123456' };
      mockAuthService.login.mockResolvedValue(undefined);
      const result = await controller.login(loginDto);
      expect(result).toBeUndefined();
      expect(authService.login).toHaveBeenCalledWith(loginDto.email, loginDto.password);
    });
  });
});
