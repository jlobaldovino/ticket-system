import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../common/guards/jwt.guard';
import { EventoAuditoriaDTO } from './audit.dto';
import { AuditService } from './audit.service';

@ApiTags('Auditoria')
@Controller('auditoria')
export class AuditController {
  constructor(private readonly auditService: AuditService) {}

  @Get()
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({
    summary: 'Listar eventos de auditoría',
    description: 'Obtiene una lista de eventos de auditoría registrados en el sistema.',
  })
  @ApiResponse({
    status: 200,
    description: 'Eventos de auditoría listados exitosamente.',
    type: [EventoAuditoriaDTO],
  })
  @ApiResponse({
    status: 400,
    description: 'Solicitud inválida. Verifica los datos enviados.',
  })
  @ApiResponse({
    status: 401,
    description: 'No autorizado. Se requiere un token válido.',
  })
  async listarEventos(): Promise<EventoAuditoriaDTO[]> {
    console.log("controller: ");
    return await this.auditService.listarEventos();
  }
}