import { Test, TestingModule } from '@nestjs/testing';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { UsersService } from './users.service';
import { NotFoundException } from '@nestjs/common';
import { of, throwError } from 'rxjs';
import { AxiosResponse } from 'axios';

const mockHttpService = {
  get: jest.fn(),
};

const mockConfigService = {
  get: jest.fn(),
};

describe('UsersService', () => {
  let service: UsersService;
  let httpService: HttpService;
  let configService: ConfigService;

  const mockUserApiUrl = 'http://mock-user-api-url.com';
  const testEmail = 'test@email.com';
  const expectedUrl = `${mockUserApiUrl}/email/${testEmail}`;

  beforeEach(async () => {
    jest.clearAllMocks();

    const module: TestingModule = await Test.createTestingModule({
      providers: [UsersService,{
            provide: HttpService,useValue: mockHttpService
        },{
            provide: ConfigService,useValue: mockConfigService
        }
      ]}).compile();

    service = module.get<UsersService>(UsersService);
    httpService = module.get<HttpService>(HttpService);
    configService = module.get<ConfigService>(ConfigService);

    mockConfigService.get.mockReturnValue(mockUserApiUrl);
  });

  it('should be defined', () => {expect(service).toBeDefined();});

  describe('obtenerPorEmail - Success', () => {
    it('Debe devolver los datos del usuario cuando se encuentren', async () => {
      const mockUserData = { id: 1, email: testEmail, name: 'Test User' };
      const mockAxiosResponse: AxiosResponse = {
        data: mockUserData,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: { headers: {} as any },
      };
      mockHttpService.get.mockReturnValue(of(mockAxiosResponse));
      const result = await service.obtenerPorEmail(testEmail);
      expect(result).toEqual(mockUserData);
      expect(configService.get).toHaveBeenCalledWith('USERS_API_URL');
      expect(httpService.get).toHaveBeenCalledTimes(1);
      expect(httpService.get).toHaveBeenCalledWith(expectedUrl);
    });
  });

  describe('obtenerPorEmail - Failure', () => {
    it('Lanzar --> NotFoundException cuando HttpService devuelve un error', async () => {
      const errorResponse = {response: { status: 404, data: 'Not Found' }};
      mockHttpService.get.mockReturnValue(throwError(() => errorResponse));
      await expect(service.obtenerPorEmail(testEmail)).rejects.toThrow(
        NotFoundException,
      );
      await expect(service.obtenerPorEmail(testEmail)).rejects.toThrow(
        'Usuario no encontrado',
      );
      expect(configService.get).toHaveBeenCalledWith('USERS_API_URL');
      expect(httpService.get).toHaveBeenCalledTimes(2);
      expect(httpService.get).toHaveBeenCalledWith(expectedUrl);
    });

     it('Lanza -> NotFoundException cuando HttpService lanza un error genérico', async () => {
       const genericError = new Error('Network Error');
       mockHttpService.get.mockReturnValue(throwError(() => genericError));
       await expect(service.obtenerPorEmail(testEmail)).rejects.toThrow(
         NotFoundException,
       );
       await expect(service.obtenerPorEmail(testEmail)).rejects.toThrow(
         'Usuario no encontrado',
       );
       expect(configService.get).toHaveBeenCalledWith('USERS_API_URL');
       expect(httpService.get).toHaveBeenCalledTimes(2);
       expect(httpService.get).toHaveBeenCalledWith(expectedUrl);
     });
  });
});