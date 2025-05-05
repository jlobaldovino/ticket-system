import { BadRequestException, Body, Controller, Delete, Get, NotFoundException, Param, ParseUUIDPipe, Post, Put, Query, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiCreatedResponse, ApiBearerAuth, ApiOkResponse, ApiParam, ApiQuery } from '@nestjs/swagger';
import { TicketsService } from './tickets.service';
import { ActualizarTicketDTO, CrearTicketDTO, TicketsRespuestaDTO } from './tickets.dto';
import { JwtAuthGuard } from 'src/common/guards/jwt.guard';

@ApiTags('Tickets')
@Controller('Tickets')
export class TicketsController {
  constructor(private readonly ticketsService: TicketsService) {}

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo tickets' })
  @ApiCreatedResponse({ type: TicketsRespuestaDTO })
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
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
  @ApiOperation({ summary: 'Actualizar ticket por ID' })
  @ApiParam({ name: 'id', description: 'UUID del ticket a actualizar' })
  @ApiOkResponse({ type: TicketsRespuestaDTO })
  async actualizar(@Param('id') id: string, @Body() dto: ActualizarTicketDTO): Promise<TicketsRespuestaDTO> {
    return await this.ticketsService.actualizarTicket(id, dto);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Eliminar ticket por ID' })
  @ApiParam({ name: 'id', description: 'UUID del ticket a Eliminar' })
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
    @ApiOperation({ summary: 'Obtener tiket por ID' })
    @ApiParam({ name: 'id', description: 'UUID del tiket' })
    @ApiOkResponse({ type: TicketsRespuestaDTO })
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
    @ApiOperation({ summary: 'Listar tikets paginados' })
    @ApiQuery({ name: 'page', required: false, type: Number })
    @ApiQuery({ name: 'size', required: false, type: Number })
    @ApiOkResponse({ type: [TicketsRespuestaDTO] })
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
    @ApiOperation({ summary: 'Filtrar tikets paginados' })
    @ApiQuery({ name: 'status', required: false, type: String, description: 'Estado del ticket (ABIERTO o CERRADO)' })
    @ApiQuery({ name: 'usuarioId', required: false, type: String, description: 'UUID del usuario asociado al ticket' })
    @ApiQuery({ name: 'page', required: false, type: Number, description: 'Número de página' })
    @ApiQuery({ name: 'size', required: false, type: Number, description: 'Tamaño de la página' })
    @ApiOkResponse({ type: [TicketsRespuestaDTO] })
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
