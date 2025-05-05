import { BadRequestException, Body, Controller, Delete, Get, NotFoundException, Param, ParseUUIDPipe, Post, Put, Query, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiCreatedResponse, ApiBearerAuth, ApiOkResponse, ApiParam, ApiQuery, ApiResponse } from '@nestjs/swagger';
import { TicketsService } from './tickets.service';
import { ActualizarTicketDTO, CrearTicketDTO, TicketsRespuestaDTO } from './tickets.dto';
import { JwtAuthGuard } from 'src/common/guards/jwt.guard';

@ApiTags('Tickets')
@Controller('Tickets')
export class TicketsController {
  constructor(private readonly ticketsService: TicketsService) {}

  @Post()
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Crear un nuevo ticket', description: 'Permite crear un nuevo ticket en el sistema.' })
  @ApiCreatedResponse({ description: 'Ticket creado exitosamente.', type: TicketsRespuestaDTO })
  @ApiResponse({ status: 400, description: 'Solicitud inválida. Verifica los datos enviados.' })
  async crear(@Body() dto: CrearTicketDTO): Promise<TicketsRespuestaDTO> {
    try {
      return await this.ticketsService.crearTicket(dto);
    } catch (error) {
      const err = error as { status: number; message: string };
      if (err.status === 400) {
        throw new BadRequestException(err.message);
      }
      throw new BadRequestException('Error inesperado');
    }
  }


  @Put(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Actualizar ticket por ID', description: 'Actualiza los datos de un ticket específico mediante su UUID.' })
  @ApiParam({ name: 'id', description: 'UUID del ticket a actualizar', example: '123e4567-e89b-12d3-a456-426614174000' })
  @ApiOkResponse({ description: 'Ticket actualizado exitosamente.', type: TicketsRespuestaDTO })
  @ApiResponse({ status: 404, description: 'Ticket no encontrado.' })
  @ApiResponse({ status: 400, description: 'Solicitud inválida. Verifica los datos enviados.' })
  async actualizar(@Param('id') id: string, @Body() dto: ActualizarTicketDTO): Promise<TicketsRespuestaDTO> {
    return await this.ticketsService.actualizarTicket(id, dto);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Eliminar ticket por ID', description: 'Elimina un ticket específico mediante su UUID.' })
  @ApiParam({ name: 'id', description: 'UUID del ticket a eliminar', example: '123e4567-e89b-12d3-a456-426614174000' })
  @ApiOkResponse({ description: 'Ticket eliminado exitosamente.', type: TicketsRespuestaDTO })
  @ApiResponse({ status: 404, description: 'Ticket no encontrado.' })
  async eliminar(@Param('id') id: string): Promise<TicketsRespuestaDTO> {
    try {
      return await this.ticketsService.eliminarTicket(id);
    } catch (error) {
      const err = error as { status: number; message: string };
      if (err.status === 404) {
        throw new NotFoundException(err.message);
      }
      throw new BadRequestException('Error inesperado');
    }
  }
  
  @Get(':id')
    @UseGuards(JwtAuthGuard)
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Obtener ticket por ID', description: 'Obtiene los detalles de un ticket específico mediante su UUID.' })
    @ApiParam({ name: 'id', description: 'UUID del ticket', example: '123e4567-e89b-12d3-a456-426614174000' })
    @ApiOkResponse({ description: 'Detalles del ticket obtenidos exitosamente.', type: TicketsRespuestaDTO })
    @ApiResponse({ status: 404, description: 'Ticket no encontrado.' })
    async obtenerTicketPorId(@Param('id', new ParseUUIDPipe()) id: string): Promise<TicketsRespuestaDTO> {
      try {
        return await this.ticketsService.obtenerPorId(id);
      } catch (error) {
        const err = error as { status: number; message: string };
        if (err.status === 404) {
          throw new NotFoundException(err.message);
        }
        throw new BadRequestException('Error inesperado');
      }
    }


  @Get()
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Listar tickets paginados', description: 'Obtiene una lista de tickets con soporte para paginación.' })
  @ApiQuery({ name: 'page', required: false, type: Number, description: 'Número de página (opcional)', example: 1 })
  @ApiQuery({ name: 'size', required: false, type: Number, description: 'Tamaño de la página (opcional)', example: 10 })
  @ApiOkResponse({ description: 'Lista de tickets obtenida exitosamente.', type: [TicketsRespuestaDTO] })
  @ApiResponse({ status: 404, description: 'No se encontraron tickets.' })
    async listarUsuarios(@Query() query: any) {
      try {
        return await this.ticketsService.obtenerTodosPagin(query);
      } catch (error) {
        const err = error as { status: number; message: string };
        if (err.status === 404) {
          throw new NotFoundException(err.message);
        }
        throw new BadRequestException('Error inesperado');
      }
    }


    @Get('filtrar')
    @UseGuards(JwtAuthGuard)
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Filtrar tickets paginados', description: 'Filtra tickets según estado, usuario y paginación.' })
    @ApiQuery({ name: 'status', required: false, type: String, description: 'Estado del ticket (ABIERTO o CERRADO)', example: 'ABIERTO' })
    @ApiQuery({ name: 'usuarioId', required: false, type: String, description: 'UUID del usuario asociado al ticket', example: '123e4567-e89b-12d3-a456-426614174000' })
    @ApiQuery({ name: 'page', required: false, type: Number, description: 'Número de página (opcional)', example: 1 })
    @ApiQuery({ name: 'size', required: false, type: Number, description: 'Tamaño de la página (opcional)', example: 10 })
    @ApiOkResponse({ description: 'Lista de tickets filtrados obtenida exitosamente.', type: [TicketsRespuestaDTO] })
    @ApiResponse({ status: 404, description: 'No se encontraron tickets.' })
    async filtrarTickets(@Query() query: any): Promise<TicketsRespuestaDTO[]> {
      try {
        return await this.ticketsService.filtrarTickets(query);
      } catch (error) {
        const err = error as { status: number; message: string };
        if (err.status === 404) {
          throw new NotFoundException(err.message);
        }
        throw new BadRequestException('Error inesperado');
      }
    }


}
