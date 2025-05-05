import { BadRequestException, Injectable, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { firstValueFrom } from 'rxjs';
import { log } from 'node:console';
import { AxiosResponse } from 'axios';
import { ActualizarTicketDTO, CrearTicketDTO, TicketsRespuestaDTO } from './tickets.dto';

@Injectable()
export class TicketsService {
  private readonly baseUrl: string;

  constructor(
    private readonly http: HttpService,
    private readonly configService: ConfigService
  ) {
    this.baseUrl = this.configService.get<string>('TICKETS_API_URL')!;
  }

  async crearTicket(dto: CrearTicketDTO): Promise<any> {
    try {
      const res$ = this.http.post(`${this.baseUrl}`, dto);
      console.log("remote: "+this.baseUrl);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      throw new BadRequestException(err?.response?.data?.message || 'Error al crear ticket');
    }
  }

  async actualizarTicket(id: string, dto: ActualizarTicketDTO): Promise<any> {
      try {
        const res$ = this.http.put(`${this.baseUrl}/${id}`, dto);
        console.log("remote: "+this.baseUrl);
        const res = await firstValueFrom(res$);
        return res.data;
      } catch (err: any) {
        if (err?.response?.status === 404) {
          throw new NotFoundException(`Ticket con ID ${id} no encontrado`);
        }
        throw new BadRequestException(err?.response?.data?.message || 'Error al actualizar');
      }
    }

    async eliminarTicket(id: string): Promise<any> {
      try {
        const res$ = this.http.delete(`${this.baseUrl}/${id}`);
        console.log("remote: "+this.baseUrl);
        const res = await firstValueFrom(res$);
        return res.data;
      } catch (err: any) {
        if (err?.response?.status === 404) {
          throw new NotFoundException(`Ticket con ID ${id} no encontrado`);
        }
        throw new BadRequestException(err?.response?.data?.message || 'Error al eliminar');
      }
    }

    async obtenerPorId(id: string): Promise<any> {
      try {
        const res$ = this.http.get(`${this.baseUrl}/${id}`);
        const res = await firstValueFrom(res$);
        return res.data;
      } catch (err: any) {
        throw new NotFoundException(`Ticket con ID ${id} no encontrado`);
      }
    }
  
    async obtenerTodosPagin(query: any): Promise<AxiosResponse<TicketsRespuestaDTO[]>> {
      try {
        const res$ = this.http.get(`${this.baseUrl}`, { params: query });
        const res = await firstValueFrom(res$);
        return res.data;
      } catch (err: any) {
        throw new UnauthorizedException('No autorizado');
      }
    }

    async filtrarTickets(query: any): Promise<TicketsRespuestaDTO[]> {
      const { page = 0, size = 5, status, usuarioId } = query; // Valores predeterminados para paginación
      const params = new URLSearchParams();
  
      // Agregar parámetros opcionales
      if (status) params.append('status', status);
      if (usuarioId) params.append('usuarioId', usuarioId);
      params.append('page', page.toString());
      params.append('size', size.toString());
  
      try {
        const res$ = this.http.get(`${this.baseUrl}`, { params: query });
        const res = await firstValueFrom(res$);
        return res.data;
      } catch (err: any) {
        throw new UnauthorizedException('No autorizado');
      }
    }
  

}