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
  @ApiOperation({ summary: 'Listar eventos de auditoría' })
  @ApiResponse({ status: 200, description: 'Eventos de auditoría listados exitosamente', type: [EventoAuditoriaDTO] })
  @ApiResponse({ status: 400, description: 'Solicitud inválida' })
  async listarEventos(): Promise<EventoAuditoriaDTO[]> {
    console.log("controller: ");
    return await this.auditService.listarEventos();
  }
}