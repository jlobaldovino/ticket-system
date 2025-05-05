import { Test, TestingModule } from '@nestjs/testing';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { AuditService } from './audit.service';
import { of, throwError } from 'rxjs';
import { AxiosRequestHeaders, AxiosResponse } from 'axios';
import { BadRequestException } from '@nestjs/common';

const mockHttpService = {
  get: jest.fn(),
};

const mockConfigService = {
  get: jest.fn(),
};

describe('AuditService', () => {
  let service: AuditService;
  let httpService: HttpService;
  let configService: ConfigService;

  const mockAuditApiUrl = 'http://mock-audit-api-url.com';
  const mockEventos = [
    {
      id: 'uuid-123',
      descripcion: 'Evento de prueba',
      fecha: new Date(),
      usuarioId: 'uuid-456',
    },
  ];

  beforeEach(async () => {
    jest.clearAllMocks();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuditService,
        { provide: HttpService, useValue: mockHttpService },
        { provide: ConfigService, useValue: mockConfigService },
      ],
    }).compile();

    service = module.get<AuditService>(AuditService);
    httpService = module.get<HttpService>(HttpService);
    configService = module.get<ConfigService>(ConfigService);

    mockConfigService.get.mockReturnValue(mockAuditApiUrl);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('listarEventos', () => {
    it('debería listar eventos exitosamente', async () => {
      const mockResponse: AxiosResponse = { data: mockEventos, status: 200, statusText: 'OK', headers: {}, config: {
          headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.get.mockReturnValue(of(mockResponse));

      const result = await service.listarEventos();
      expect(result).toEqual(mockEventos);
      expect(httpService.get).toHaveBeenCalledWith(mockAuditApiUrl);
    });

    it('debería lanzar BadRequestException si ocurre un error', async () => {
      mockHttpService.get.mockReturnValue(throwError(() => ({ response: { data: { message: 'Error al listar' }, status: 400 } })));

      await expect(service.listarEventos()).rejects.toThrow(BadRequestException);
      expect(httpService.get).toHaveBeenCalledWith(mockAuditApiUrl);
    });
  });
});