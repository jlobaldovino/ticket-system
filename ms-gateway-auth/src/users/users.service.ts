import { BadRequestException, Injectable, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { firstValueFrom } from 'rxjs';
import { log } from 'node:console';
import { ActualizarUsuarioDTO, CrearUsuarioDTO, UsuarioRespuestaDTO } from 'src/auth/auth.dto';
import { AxiosResponse } from 'axios';

@Injectable()
export class UsersService {
  private readonly baseUrl: string;

  constructor(
    private readonly http: HttpService,
    private readonly configService: ConfigService
  ) {
    this.baseUrl = this.configService.get<string>('USERS_API_URL')!;
  }

  async obtenerPorEmail(email: string): Promise<any> {
    try {
      const url = `${this.baseUrl}/email/${email}`;
      log('URL:', url);
      const res$ = this.http.get(url);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err) {
      throw new NotFoundException('Usuario no encontrado');
    }
  }

  async crearUsuario(dto: CrearUsuarioDTO): Promise<any> {
    try {
      const res$ = this.http.post(`${this.baseUrl}`, dto);
      console.log("remote: "+this.baseUrl);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      throw new BadRequestException(err?.response?.data?.message || 'Error al crear usuario');
    }
  }

  async obtenerPorId(id: string): Promise<any> {
    try {
      const res$ = this.http.get(`${this.baseUrl}/${id}`);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      throw new NotFoundException(`Usuario con ID ${id} no encontrado`);
    }
  }

  async obtenerTodos(query: any): Promise<AxiosResponse<UsuarioRespuestaDTO[]>> {
    try {
      const res$ = this.http.get(`${this.baseUrl}`, { params: query });
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      throw new UnauthorizedException('No autorizado');
    }
  }

  async actualizarUsuario(id: string, dto: ActualizarUsuarioDTO): Promise<any> {
    try {
      const res$ = this.http.put(`${this.baseUrl}/${id}`, dto);
      console.log("remote: "+this.baseUrl);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      if (err?.response?.status === 404) {
        throw new NotFoundException(`Usuario con ID ${id} no encontrado`);
      }
      throw new BadRequestException(err?.response?.data?.message || 'Error al actualizar');
    }
  }
  
}