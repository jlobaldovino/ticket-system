import { Type } from 'class-transformer';
import {IsEnum, IsNotEmpty, IsString, IsUUID} from 'class-validator';

export class CrearTicketDTO {
  
  @IsNotEmpty()
  descripcion!: string;

  @IsNotEmpty()
  usuarioId!: string;
}

export class ActualizarTicketDTO {
  
  @IsNotEmpty()
  descripcion!: string;

  @IsNotEmpty()
  status!: string;
}

export class TicketsRespuestaDTO {
  @IsUUID()
  id!: string;

  @IsNotEmpty()
  @IsString()
  descripcion!: string;

  @IsUUID()
  usuarioId!: string;

  @Type(() => Date)
  fechaCreacion!: Date;

  @Type(() => Date)
  fechaActualizacion!: Date;

  @IsEnum(['ABIERTO', 'CERRADO'])
  status!: 'ABIERTO' | 'CERRADO';
}
