import { Injectable, BadRequestException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { firstValueFrom } from 'rxjs';
import { EventoAuditoriaDTO } from './audit.dto';

@Injectable()
export class AuditService {
  private readonly baseUrl: string;

  constructor(
    private readonly http: HttpService,
    private readonly configService: ConfigService,
  ) {
    this.baseUrl = this.configService.get<string>('AUDIT_API_URL')!;
  }

  async listarEventos(): Promise<EventoAuditoriaDTO[]> {
    try {
      const res$ = this.http.get(`${this.baseUrl}`);
      console.log("remote: "+this.baseUrl);
      const res = await firstValueFrom(res$);
      return res.data;
    } catch (err: any) {
      throw new BadRequestException(err?.response?.data?.message || 'Error al listar eventos de auditoría');
    }
  }
}