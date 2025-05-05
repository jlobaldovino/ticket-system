import { BadRequestException, Body, Controller, Get, NotFoundException, Param, ParseUUIDPipe, Post, Put, Query, UseGuards } from '@nestjs/common';
import { UsersService } from './users.service';
import { ApiTags, ApiOperation, ApiCreatedResponse, ApiBearerAuth, ApiOkResponse, ApiParam, ApiQuery, ApiResponse } from '@nestjs/swagger';
import { UsuarioRespuestaDTO, CrearUsuarioDTO, ActualizarUsuarioDTO } from 'src/auth/auth.dto';
import { JwtAuthGuard } from 'src/common/guards/jwt.guard';

@ApiTags('Usuarios')
@Controller('usuarios')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo usuario', description: 'Permite crear un nuevo usuario en el sistema.' })
  @ApiCreatedResponse({ description: 'Usuario creado exitosamente.', type: UsuarioRespuestaDTO })
  @ApiResponse({ status: 400, description: 'Solicitud inválida. Verifica los datos enviados.' })
  async crear(@Body() dto: CrearUsuarioDTO): Promise<UsuarioRespuestaDTO> {
    try {
      return await this.usersService.crearUsuario(dto);
    } catch (error) {
      const err = error as { status: number; message: string };
      if (err.status === 400) {
        throw new BadRequestException(err.message);
      }
      throw new BadRequestException('Error inesperado');
    }
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Obtener usuario por ID', description: 'Obtiene los detalles de un usuario específico mediante su UUID.' })
  @ApiParam({ name: 'id', description: 'UUID del usuario', example: '123e4567-e89b-12d3-a456-426614174000' })
  @ApiOkResponse({ description: 'Detalles del usuario obtenidos exitosamente.', type: UsuarioRespuestaDTO })
  @ApiResponse({ status: 404, description: 'Usuario no encontrado.' })
  async obtenerPorId(@Param('id', new ParseUUIDPipe()) id: string): Promise<UsuarioRespuestaDTO> {
    try {
      return await this.usersService.obtenerPorId(id);
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
  @ApiOperation({ summary: 'Listar usuarios paginados', description: 'Obtiene una lista de usuarios con soporte para paginación y filtros.' })
  @ApiQuery({ name: 'page', required: false, type: Number, description: 'Número de página (opcional)', example: 1 })
  @ApiQuery({ name: 'size', required: false, type: Number, description: 'Tamaño de la página (opcional)', example: 10 })
  @ApiQuery({ name: 'rol', required: false, type: String, description: 'Rol del usuario (opcional)', example: 'ADMIN' })
  @ApiOkResponse({ description: 'Lista de usuarios obtenida exitosamente.', type: [UsuarioRespuestaDTO] })
  @ApiResponse({ status: 404, description: 'No se encontraron usuarios.' })
  async listarUsuarios(@Query() query: any) {
    try {
      return await this.usersService.obtenerTodos(query);
    } catch (error) {
      const err = error as { status: number; message: string };
      if (err.status === 404) {
        throw new NotFoundException(err.message);
      }
      throw new BadRequestException('Error inesperado');
    }
  }
  
  @Put(':id')
  @UseGuards(JwtAuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Actualizar usuario por ID', description: 'Actualiza los datos de un usuario específico mediante su UUID.' })
  @ApiParam({ name: 'id', description: 'UUID del usuario a actualizar', example: '123e4567-e89b-12d3-a456-426614174000' })
  @ApiOkResponse({ description: 'Usuario actualizado exitosamente.', type: UsuarioRespuestaDTO })
  @ApiResponse({ status: 404, description: 'Usuario no encontrado.' })
  @ApiResponse({ status: 400, description: 'Solicitud inválida. Verifica los datos enviados.' })
  async actualizar(@Param('id') id: string, @Body() dto: ActualizarUsuarioDTO): Promise<UsuarioRespuestaDTO> {
    try {
      return await this.usersService.actualizarUsuario(id, dto);
    } catch (error) {
      const err = error as { status: number; message: string };
      if (err.status === 404) {
        throw new NotFoundException(err.message);
      }
      throw new BadRequestException('Error inesperado');
    }
  }




}
