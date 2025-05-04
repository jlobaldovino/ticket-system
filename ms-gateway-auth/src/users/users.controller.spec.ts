import { Test, TestingModule } from '@nestjs/testing';
import { UsersController } from './users.controller';
import { UsersService } from './users.service';
import { JwtAuthGuard } from '../common/guards/jwt.guard';
import { UsuarioRespuestaDTO, CrearUsuarioDTO } from '../auth/auth.dto';

describe('UsersController - Crear Usuario', () => {
  let controller: UsersController;
  let service: UsersService;
  const mockUser: UsuarioRespuestaDTO = {
    id: 'uuid-123',
    nombres: 'Juan',
    apellidos: 'Pérez',
    email: 'juan@email.com',
    rol: 'USER',
  };
  const mockUsersService = {
    crearUsuario: jest.fn().mockResolvedValue(mockUser),
  };
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [{ provide: UsersService, useValue: mockUsersService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();
    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
  });
  it('debería crear un usuario exitosamente', async () => {
    const dto: CrearUsuarioDTO = {
      nombres: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@email.com',
      password: 'clave123',
    };
    const result = await controller.crear(dto);
    expect(result).toEqual(mockUser);
    expect(service.crearUsuario).toHaveBeenCalledWith(dto);
  });
  it('debería lanzar BadRequestException si el email ya existe', async () => {
    const dto: CrearUsuarioDTO = {
      nombres: 'Ana',
      apellidos: 'Rojas',
      email: 'ana@email.com',
      password: 'clave123',
    };  
    mockUsersService.crearUsuario = jest.fn().mockRejectedValue({
      response: { data: { message: 'El email ya está registrado' } },
    });  
    await expect(controller.crear(dto)).rejects.toThrow();
    expect(mockUsersService.crearUsuario).toHaveBeenCalledWith(dto);
  });  
  it('debería lanzar BadRequestException ante un error inesperado', async () => {
    const dto: CrearUsuarioDTO = {
      nombres: 'Luis',
      apellidos: 'Martínez',
      email: 'luis@email.com',
      password: 'clave123',
    };
    mockUsersService.crearUsuario = jest.fn().mockRejectedValue(new Error('Error inesperado'));
    await expect(controller.crear(dto)).rejects.toThrow();
    expect(mockUsersService.crearUsuario).toHaveBeenCalledWith(dto);
  });
});

describe('UsersController - Actualizar Usuario', () => {
  let controller: UsersController;
  let service: UsersService;
  const mockUser: UsuarioRespuestaDTO = {
    id: 'uuid-456',
    nombres: 'Pedro',
    apellidos: 'González',
    email: 'pedro@email.com',
    rol: 'USER',
  };
  const mockUsersService = {
    actualizarUsuario: jest.fn().mockResolvedValue(mockUser),
  };
  beforeEach(() => {
    mockUsersService.actualizarUsuario = jest.fn().mockResolvedValue(mockUser);
  });
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [{ provide: UsersService, useValue: mockUsersService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();
    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
  });
  it('debería actualizar el usuario correctamente', async () => {
    const dto = {
      nombres: 'Pedro',
      apellidos: 'González',
      email: 'pedro@email.com',
    };
    const id = 'uuid-456';
    const result = await controller.actualizar(id, dto);
    expect(result).toEqual(mockUser);
    expect(mockUsersService.actualizarUsuario).toHaveBeenCalledWith(id, dto);
  });
  it('debería lanzar NotFoundException si el usuario no existe', async () => {
    const id = 'uuid-999';
    const dto = { nombres: 'Nuevo' };
    mockUsersService.actualizarUsuario = jest.fn().mockRejectedValue({
      status: 404,
      message: 'Usuario con ID uuid-999 no encontrado',
    });  
    await expect(controller.actualizar(id, dto)).rejects.toThrow();
    expect(mockUsersService.actualizarUsuario).toHaveBeenCalledWith(id, dto);
  });  
  it('debería lanzar BadRequestException ante error inesperado', async () => {
    const id = 'uuid-888';
    const dto = { email: 'correo@invalido' };  
    mockUsersService.actualizarUsuario = jest.fn().mockRejectedValue({
      response: { data: { message: 'Error inesperado' } },
    });  
    await expect(controller.actualizar(id, dto)).rejects.toThrow();
    expect(mockUsersService.actualizarUsuario).toHaveBeenCalledWith(id, dto);
  });
});

describe('UsersController - Listar Usuarios', () => {
  let controller: UsersController;
  let service: UsersService;
  const mockUsers: UsuarioRespuestaDTO[] = [
    {
      id: 'uuid-123',
      nombres: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@email.com',
      rol: 'USER',
    },
    {
      id: 'uuid-456',
      nombres: 'Ana',
      apellidos: 'Rojas',
      email: 'ana@email.com',
      rol: 'ADMIN',
    },
  ];
  const mockUsersService = {
    obtenerTodos: jest.fn().mockResolvedValue(mockUsers),
  };
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [{ provide: UsersService, useValue: mockUsersService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();
    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
  });
  it('debería listar usuarios exitosamente', async () => {
    const query = { page: 1, size: 10, rol: 'USER' };
    const result = await controller.listarUsuarios(query);
    expect(result).toEqual(mockUsers);
    expect(service.obtenerTodos).toHaveBeenCalledWith(query);
  });
  it('debería lanzar NotFoundException si no se encuentran usuarios', async () => {
    const query = { page: 1, size: 10, rol: 'USER' };
    mockUsersService.obtenerTodos = jest.fn().mockRejectedValue({
      status: 404,
      message: 'No se encontraron usuarios',
    });
    await expect(controller.listarUsuarios(query)).rejects.toThrow();
    expect(mockUsersService.obtenerTodos).toHaveBeenCalledWith(query);
  });

  it('debería lanzar BadRequestException ante un error inesperado', async () => {
    const query = { page: 1, size: 10, rol: 'USER' };
    mockUsersService.obtenerTodos = jest.fn().mockRejectedValue(new Error('Error inesperado'));
    await expect(controller.listarUsuarios(query)).rejects.toThrow();
    expect(mockUsersService.obtenerTodos).toHaveBeenCalledWith(query);
  });
});

describe('UsersController - Obtener por Id', () => {
  let controller: UsersController;
  let service: UsersService;
  const mockUser: UsuarioRespuestaDTO = {
    id: 'uuid-123',
    nombres: 'Juan',
    apellidos: 'Pérez',
    email: 'juan@email.com',
    rol: 'USER',
  };
  const mockUsersService = {
    obtenerPorId: jest.fn().mockResolvedValue(mockUser),
  };
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [{ provide: UsersService, useValue: mockUsersService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();
    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
  });
  it('debería obtener un usuario por ID exitosamente', async () => {
    const id = 'uuid-123';
    const result = await controller.obtenerPorId(id);
    expect(result).toEqual(mockUser);
    expect(service.obtenerPorId).toHaveBeenCalledWith(id);
  });
  it('debería lanzar NotFoundException si el usuario no existe', async () => {
    const id = 'uuid-999';
    mockUsersService.obtenerPorId = jest.fn().mockRejectedValue({
      status: 404,
      message: 'Usuario con ID uuid-999 no encontrado',
    });
    await expect(controller.obtenerPorId(id)).rejects.toThrow();
    expect(mockUsersService.obtenerPorId).toHaveBeenCalledWith(id);
  });
  it('debería lanzar BadRequestException ante un error inesperado', async () => {
    const id = 'uuid-888';
    mockUsersService.obtenerPorId = jest.fn().mockRejectedValue(new Error('Error inesperado'));
    await expect(controller.obtenerPorId(id)).rejects.toThrow();
    expect(mockUsersService.obtenerPorId).toHaveBeenCalledWith(id);
  });
});