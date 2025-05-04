import { BadRequestException, Body, Controller, Get, NotFoundException, Param, ParseUUIDPipe, Post, Put, Query, UseGuards } from '@nestjs/common';
import { UsersService } from './users.service';
import { ApiTags, ApiOperation, ApiCreatedResponse, ApiBearerAuth, ApiOkResponse, ApiParam, ApiQuery } from '@nestjs/swagger';
import { UsuarioRespuestaDTO, CrearUsuarioDTO, ActualizarUsuarioDTO } from 'src/auth/auth.dto';
import { JwtAuthGuard } from 'src/common/guards/jwt.guard';

@ApiTags('Usuarios')
@Controller('usuarios')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo usuario' })
  @ApiCreatedResponse({ type: UsuarioRespuestaDTO })
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
  @ApiOperation({ summary: 'Obtener usuario por ID' })
  @ApiParam({ name: 'id', description: 'UUID del usuario' })
  @ApiOkResponse({ type: UsuarioRespuestaDTO })
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
  @ApiOperation({ summary: 'Listar usuarios paginados' })
  @ApiQuery({ name: 'page', required: false, type: Number })
  @ApiQuery({ name: 'size', required: false, type: Number })
  @ApiQuery({ name: 'rol', required: false, type: String })
  @ApiOkResponse({ type: [UsuarioRespuestaDTO] })
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
  @ApiOperation({ summary: 'Actualizar usuario por ID' })
  @ApiParam({ name: 'id', description: 'UUID del usuario a actualizar' })
  @ApiOkResponse({ type: UsuarioRespuestaDTO })
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
