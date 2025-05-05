import { Test, TestingModule } from '@nestjs/testing';
import { AuditController } from './audit.controller';
import { AuditService } from './audit.service';
import { JwtAuthGuard } from '../common/guards/jwt.guard';
import { EventoAuditoriaDTO } from './audit.dto';

describe('AuditController', () => {
  let controller: AuditController;
  let service: AuditService;

  const mockEventos: EventoAuditoriaDTO[] = [
    {
      id: 'uuid-123',
      descripcion: 'Evento de prueba',
      fecha: new Date(),
      usuarioId: 'uuid-456',
    },
  ];

  const mockAuditService = {
    listarEventos: jest.fn().mockResolvedValue(mockEventos),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuditController],
      providers: [{ provide: AuditService, useValue: mockAuditService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();

    controller = module.get<AuditController>(AuditController);
    service = module.get<AuditService>(AuditService);
  });

  it('debería estar definido', () => {
    expect(controller).toBeDefined();
  });

  describe('listarEventos', () => {
    it('debería listar eventos exitosamente', async () => {
      const result = await controller.listarEventos();
      expect(result).toEqual(mockEventos);
      expect(service.listarEventos).toHaveBeenCalled();
    });

    it('debería lanzar una excepción si ocurre un error', async () => {
      mockAuditService.listarEventos.mockRejectedValue(new Error('Error inesperado'));
      await expect(controller.listarEventos()).rejects.toThrow('Error inesperado');
    });
  });
});